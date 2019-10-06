package de.ahahn94.coboli.menus

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.FragmentManager
import de.ahahn94.coboli.R
import de.ahahn94.coboli.fragments.ItemDetailFragment
import de.ahahn94.coboli.model.views.CachedVolumesView
import de.ahahn94.coboli.repositories.VolumeRepo

/**
 * Class that handles the popup menu on the volume card.
 */
class VolumePopupMenu(
    context: Context,
    view: View,
    gravity: Int,
    volume: CachedVolumesView?,
    fragmentManager: FragmentManager
) :
    PopupMenu(context, view, gravity) {
    init {

        // Load the menu.
        menuInflater.inflate(R.menu.menu_volume_popup, menu)

        // Bind actions to menu entries.
        setOnMenuItemClickListener {
            when (it.itemId) {

                // Show details as an overlay.
                R.id.action_details -> {
                    val dialog = ItemDetailFragment()
                    if (volume != null) {
                        with(volume) {
                            dialog.updateContent(imageFileURL, name, description)
                        }
                    }
                    val transaction = fragmentManager.beginTransaction()
                    dialog.show(transaction, "Details")
                    true
                }

                // Mark the volumes issues (and thus the volume) as (un-)read.
                R.id.action_read -> {
                    if (volume != null) {
                        VolumeRepo.switchReadStatus(volume)
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