package de.ahahn94.manhattan.menus

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import de.ahahn94.manhattan.R

/**
 * Class that handles the popup menu on the ReaderActivity.
 */
class ReaderPopupMenu(
    context: Context,
    view: View,
    gravity: Int
) :
    PopupMenu(context, view, gravity) {
    init {

        // Load the menu.
        menuInflater.inflate(R.menu.reader_popup_menu, menu)
    }
}
