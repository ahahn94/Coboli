package de.ahahn94.coboli.adapters

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.ahahn94.coboli.R
import de.ahahn94.coboli.fragments.LicenseFragment
import de.ahahn94.coboli.repositories.AboutRepo.Library

/**
 * ListAdapter for AboutRepo.Library datasets.
 * Provides the data for a RecyclerView to display.
 */
class LibrariesAdapter(
    private val fragmentManager: FragmentManager,
    private val context: Context
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
        return LibraryHolder(fragmentManager, view, context)
    }

    /**
     * Holder for AboutRepo.Library datasets.
     * Organizes the library data in a CardView.
     */
    class LibraryHolder(
        fragmentManager: FragmentManager,
        itemView: View,
        context: Context
    ) :
        RecyclerView.ViewHolder(itemView) {

        // Data object
        var library: Library? = null

        // UI elements.
        private val libraryCard: CardView = itemView.findViewById(R.id.card_library)
        private val websiteButton: TextView = itemView.findViewById(R.id.button_website)
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

            // Set OnClickListener on the button_website to open the project website in a web browser.
            websiteButton.setOnClickListener {
                val website = library?.website
                if (website != null) {
                    with(context) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(website))
                        try {
                            startActivity(intent)
                        } catch (exception: ActivityNotFoundException) {
                            Toast.makeText(
                                context,
                                getString(R.string.toast_broken_url),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }

        }

    }
}