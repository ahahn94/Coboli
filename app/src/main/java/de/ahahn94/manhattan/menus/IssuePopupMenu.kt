package de.ahahn94.manhattan.menus

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.FragmentManager
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.cache.ComicsCache
import de.ahahn94.manhattan.fragments.ItemDetailFragment
import de.ahahn94.manhattan.model.views.CachedIssuesView
import de.ahahn94.manhattan.repositories.IssueRepo
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

        // Show download/delete action based on current caching status.
        if (issue?.isCached == "1"){
            menu.findItem(R.id.action_download).isVisible = false
            menu.findItem(R.id.action_delete).isVisible = true
        } else {
            menu.findItem(R.id.action_download).isVisible = true
            menu.findItem(R.id.action_delete).isVisible = false
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
                    ComicsCache.deleteComicFile(issue?.id?:"")
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