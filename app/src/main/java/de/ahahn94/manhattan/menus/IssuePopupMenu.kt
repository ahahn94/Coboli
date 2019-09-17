package de.ahahn94.manhattan.menus

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.cache.ComicsCache
import de.ahahn94.manhattan.fragments.ItemDetailFragment
import de.ahahn94.manhattan.model.views.CachedIssuesView
import de.ahahn94.manhattan.repositories.IssueRepo
import de.ahahn94.manhattan.utils.MimeTypes
import java.lang.ref.WeakReference

/**
 * Class that handles the popup menu on the issue card.
 */
class IssuePopupMenu(
    context: Context,
    view: View,
    gravity: Int,
    issue: CachedIssuesView?,
    fragmentManager: WeakReference<FragmentManager>
) :
    PopupMenu(context, view, gravity) {
    init {

        // Load the menu.
        menuInflater.inflate(R.menu.issue_popup_menu, menu)

        // Show download/delete/open actions based on current caching status.
        if (issue?.isCached == "1") {
            menu.findItem(R.id.action_download).isVisible = false
            menu.findItem(R.id.action_delete).isVisible = true
            menu.findItem(R.id.action_open_with).isVisible = true
        } else {
            menu.findItem(R.id.action_download).isVisible = true
            menu.findItem(R.id.action_delete).isVisible = false
            menu.findItem(R.id.action_open_with).isVisible = false
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

                // Mark the issues as (un-)read.
                R.id.action_read -> {
                    if (issue != null) {
                        IssueRepo.switchReadStatus(issue)
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

                // Show the "open with" dialog that lets the user choose another app to open the
                // downloaded comic file with.
                R.id.action_open_with -> {
                    // Get the file metadata.
                    val file = ComicsCache.getFile(issue?.cachedComic?.fileName ?: "")
                    if (file != null) {
                        with(context) {
                            // Get the URI of the file.
                            val uri = FileProvider.getUriForFile(
                                this,
                                "de.ahahn94.manhattan.fileprovider", file
                            )

                            // Get the mime type.
                            val mime = MimeTypes.getMimeType(file.name)

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

                // Else do nothing.
                else -> {
                    false
                }
            }
        }
    }
}