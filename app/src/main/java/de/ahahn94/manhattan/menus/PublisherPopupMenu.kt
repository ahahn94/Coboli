package de.ahahn94.manhattan.menus

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.FragmentManager
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.fragments.ItemDetailFragment
import de.ahahn94.manhattan.model.entities.PublisherEntity

/**
 * Class that handles the popup menu on the publisher card.
 */
class PublisherPopupMenu(
    context: Context,
    view: View,
    publisherEntity: PublisherEntity?,
    fragmentManager: FragmentManager
) :
    PopupMenu(context, view) {
    init {

        // Load the menu.
        menuInflater.inflate(R.menu.publisher_popup_menu, menu)

        // Bind actions to menu entries.
        setOnMenuItemClickListener {
            when (it.itemId) {

                // Show details as an overlay.
                R.id.action_details -> {
                    val dialog = ItemDetailFragment()
                    if (publisherEntity != null) {
                        with(publisherEntity) {
                            dialog.updateContent(imageFileURL, name, description)
                        }
                    }
                    val transaction = fragmentManager.beginTransaction()
                    dialog.show(transaction, "Details")
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