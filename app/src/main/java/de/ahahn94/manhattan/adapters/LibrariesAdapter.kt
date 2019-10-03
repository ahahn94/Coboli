package de.ahahn94.manhattan.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.fragments.LicenseFragment
import de.ahahn94.manhattan.repositories.AboutRepo.Library

/**
 * ListAdapter for AboutRepo.Library datasets.
 * Provides the data for a RecyclerView to display.
 */
class LibrariesAdapter(
    private val fragmentManager: FragmentManager
) :
    ListAdapter<Library, LibrariesAdapter.LibraryHolder>(
        object : DiffUtil.ItemCallback<Library>() {
            override fun areItemsTheSame(
                oldItem: Library,
                newItem: Library
            ): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(
                oldItem: Library,
                newItem: Library
            ): Boolean {
                return oldItem == newItem
            }

        }
    ) {

    /**
     * Bind data to the holder.
     * Will set the content to display in the RecyclerView.
     */
    override fun onBindViewHolder(holder: LibraryHolder, position: Int) {
        val library = getItem(position)!!

        with(holder) {

            // Add data object.
            this.library = library

            // Fill TextViews.
            name.text = library.name
            licenseName.text = library.licenseName
        }
    }

    /**
     * Initialize the holder with a layout.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_card_library, parent, false)
        return LibraryHolder(fragmentManager, view)
    }

    /**
     * Holder for AboutRepo.Library datasets.
     * Organizes the library data in a CardView.
     */
    class LibraryHolder(
        fragmentManager: FragmentManager,
        itemView: View
    ) :
        RecyclerView.ViewHolder(itemView) {

        // Data object
        var library: Library? = null

        // UI elements.
        private val libraryCard: CardView = itemView.findViewById(R.id.card_library)
        val name: TextView
        val licenseName: TextView

        init {
            name = libraryCard.findViewById(R.id.label_name)
            licenseName = libraryCard.findViewById(R.id.label_license_name)

            // Set OnClickListener on the card to open the license text as a dialog fragment.
            libraryCard.setOnClickListener {
                // Show LicenseFragment as a dialog.
                val dialog = LicenseFragment()
                val bundle = Bundle()
                bundle.putString(LicenseFragment.LICENSE_TEXT, library?.licenseText)
                dialog.arguments = bundle
                val transaction = fragmentManager.beginTransaction()
                dialog.show(transaction, LicenseFragment.TAG)
            }

        }

    }
}