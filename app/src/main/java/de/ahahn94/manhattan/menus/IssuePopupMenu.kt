package de.ahahn94.manhattan.menus

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.FragmentManager
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.fragments.ItemDetailFragment
import de.ahahn94.manhattan.model.entities.IssueEntity
import de.ahahn94.manhattan.repositories.IssueRepo
import java.lang.ref.WeakReference

/**
 * Class that handles the popup menu on the issue card.
 */
class IssuePopupMenu(
    context: Context,
    view: View,
    gravity: Int,
    issueEntity: IssueEntity?,
    fragmentManager: WeakReference<FragmentManager>
) :
    PopupMenu(context, view, gravity) {
    init {

        // Load the menu.
        menuInflater.inflate(R.menu.volume_popup_menu, menu)

        // Bind actions to menu entries.
        setOnMenuItemClickListener {
            when (it.itemId) {

                // Show details as an overlay.
                R.id.action_details -> {
                    val dialog = ItemDetailFragment()
                    if (issueEntity != null) {
                        with(issueEntity) {
                            dialog.updateContent(imageFileURL, name ?: "", description)
                        }
                    }
                    val transaction = fragmentManager.get()?.beginTransaction()
                    dialog.show(transaction, "Details")
                    true
                }

                // Mark the issues as (un-)read.
                R.id.action_read -> {
                    if (issueEntity != null) {
                        IssueRepo.switchReadStatus(issueEntity)
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