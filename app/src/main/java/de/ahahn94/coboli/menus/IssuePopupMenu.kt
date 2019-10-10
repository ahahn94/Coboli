package de.ahahn94.coboli.menus

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import de.ahahn94.coboli.R
import de.ahahn94.coboli.activities.ReaderActivity
import de.ahahn94.coboli.cache.ComicsCache
import de.ahahn94.coboli.fragments.ItemDetailFragment
import de.ahahn94.coboli.model.views.CachedIssuesView
import de.ahahn94.coboli.repositories.IssueRepo
import de.ahahn94.coboli.utils.FileTypes
import de.ahahn94.coboli.utils.network.OnlineStatusManager
import java.lang.ref.WeakReference

/**
 * Class that handles the popup menu on the issue card.
 */
class IssuePopupMenu(
    context: Context,
    view: View,
    gravity: Int,
    issue: CachedIssuesView?,
    fragmentManager: WeakReference<FragmentManager>,
    connected: Boolean
) :
    PopupMenu(context, view, gravity) {
    init {

        // Load the menu.
        menuInflater.inflate(R.menu.menu_issue_popup, menu)

        // Show download/delete/open/share actions based on current caching status.
        if (issue?.isCached == true) {
            menu.findItem(R.id.action_download).isVisible = false
            menu.findItem(R.id.action_delete).isVisible = true
            menu.findItem(R.id.action_open_with).isVisible = true
            menu.findItem(R.id.action_open).isVisible = issue.cachedComic?.readable == true
            menu.findItem(R.id.action_share).isVisible = true
        } else {
            // Only show download button if connected to API endpoint.
            menu.findItem(R.id.action_download).isVisible = connected
            menu.findItem(R.id.action_delete).isVisible = false
            menu.findItem(R.id.action_open_with).isVisible = false
            menu.findItem(R.id.action_open).isVisible = false
            menu.findItem(R.id.action_share).isVisible = false
        }

        // Show mark as unread/in progress/read based on current readStatus.
        when (issue?.readStatus?.isRead) {
            false -> {
                // Not read or in progress.
                if (issue.readStatus.currentPage == 0) {
                    // Unread. Show mark as in progress & read.
                    menu.findItem(R.id.action_read).isVisible = true
                    menu.findItem(R.id.action_in_progress).isVisible = true
                    menu.findItem(R.id.action_unread).isVisible = false
                } else {
                    // In progress. Show mark as unread & read.
                    menu.findItem(R.id.action_read).isVisible = true
                    menu.findItem(R.id.action_in_progress).isVisible = false
                    menu.findItem(R.id.action_unread).isVisible = true
                }
            }
            true -> {
                // Is read. Show mark as unread & in progress.
                menu.findItem(R.id.action_read).isVisible = false
                menu.findItem(R.id.action_in_progress).isVisible = true
                menu.findItem(R.id.action_unread).isVisible = true
            }

        }

        // Bind actions to menu entries.
        setOnMenuItemClickListener {
            when (it.itemId) {

                // Show details as an overlay.
                R.id.action_details -> {
                    val dialog = ItemDetailFragment()
                    if (issue != null) {
                        with(issue) {
                            dialog.updateContent(imageFileURL, name ?: "", description)
                        }
                    }
                    val transaction = fragmentManager.get()?.beginTransaction()
                    dialog.show(transaction!!, "Details")
                    true
                }

                // Mark the issues as read.
                R.id.action_read -> {
                    if (issue != null) {
                        IssueRepo.switchReadStatus(issue, IssueRepo.ReadStatus.READ)
                    }
                    true
                }

                // Mark the issues as in progress.
                R.id.action_in_progress -> {
                    if (issue != null) {
                        IssueRepo.switchReadStatus(issue, IssueRepo.ReadStatus.IN_PROGRESS)
                    }
                    true
                }

                // Mark the issues as unread.
                R.id.action_unread -> {
                    if (issue != null) {
                        IssueRepo.switchReadStatus(issue, IssueRepo.ReadStatus.UNREAD)
                    }
                    true
                }

                // Download the comic file.
                R.id.action_download -> {
                    ComicsCache.cacheComicFile(issue?.id ?: "")
                    true
                }

                // Delete the downloaded comic file.
                R.id.action_delete -> {
                    ComicsCache.deleteComicFile(issue?.id ?: "")
                    true
                }

                // Open the comic file in the ReaderActivity.
                // Only works for a few file types (see ComicsCache).
                R.id.action_open -> {
                    val intent = Intent(context, ReaderActivity::class.java)

                    val bundle = Bundle()
                    bundle.putSerializable(ReaderActivity.ISSUE, issue)
                    intent.putExtras(bundle)

                    startActivity(context, intent, null)
                    true
                }

                // Show the "open with" dialog that lets the user choose another app to open the
                // downloaded comic file with.
                // Which applications are shown depends on which apps are installed and whether the apps
                // implement support for opening files with them via the "open with" action.
                R.id.action_open_with -> {
                    // Get the file metadata.
                    val file = ComicsCache.getFile(issue?.cachedComic?.fileName ?: "")
                    if (file != null) {
                        with(context) {
                            // Get the URI of the file.
                            val uri = FileProvider.getUriForFile(
                                this,
                                "de.ahahn94.coboli.fileprovider", file
                            )

                            // Get the mime type.
                            val mime = FileTypes.getMimeType(file.name)

                            // Create the activity.
                            val intent = Intent()
                            intent.action = Intent.ACTION_VIEW
                            intent.setDataAndType(uri, mime)
                            // Grant read-only permissions to the activity/app.
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            try {
                                startActivity(intent)
                            } catch (exception: ActivityNotFoundException) {
                                Toast.makeText(
                                    context,
                                    getString(R.string.no_app_for_mime_type, mime),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    true
                }

                // Show the "share" dialog that lets the user choose another app to share the
                // downloaded comic file with (e.g. e-mail app, Google Drive).
                // Which applications are shown depends on which apps are installed and whether the apps
                // implement support for sharing files with them via the "send" action.
                R.id.action_share -> {
                    // Get the file metadata.
                    val fileName = issue?.cachedComic?.fileName ?: ""
                    val file = ComicsCache.getFile(fileName)
                    if (file != null) {
                        with(context) {
                            // Get the URI of the file.
                            val uri = FileProvider.getUriForFile(
                                this,
                                "de.ahahn94.coboli.fileprovider", file
                            )

                            // Get the mime type.
                            val mime = FileTypes.getMimeType(file.name)

                            // Create the activity.
                            val intent = Intent()
                            intent.action = Intent.ACTION_SEND
                            intent.type = mime
                            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uri.toString()))
                            intent.putExtra(Intent.EXTRA_SUBJECT, fileName)
                            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share))
                            try {
                                startActivity(intent)
                            } catch (exception: ActivityNotFoundException) {
                                Toast.makeText(
                                    context,
                                    getString(R.string.no_app_for_mime_type, mime),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    true
                }

                // Else do nothing.
                else -> {
                    false
                }
            }
        }
    }
}