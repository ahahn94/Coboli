package de.ahahn94.manhattan.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.activities.FragmentedActivity
import de.ahahn94.manhattan.fragments.VolumesFragment
import de.ahahn94.manhattan.menus.PublisherPopupMenu
import de.ahahn94.manhattan.model.entities.PublisherEntity
import de.ahahn94.manhattan.utils.Localization
import java.lang.ref.WeakReference

/**
 * PagedListAdapter for PublisherEntity datasets.
 * Provides the data for a RecyclerView to display.
 */
class PublishersAdapter(
    private val fragmentManager: FragmentManager,
    private val activity: FragmentedActivity,
    val publishers: LiveData<PagedList<PublisherEntity>>
) :
    PagedListAdapter<PublisherEntity, PublishersAdapter.PublisherHolder>(
        object : DiffUtil.ItemCallback<PublisherEntity>() {
            override fun areItemsTheSame(
                oldItem: PublisherEntity,
                newItem: PublisherEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: PublisherEntity,
                newItem: PublisherEntity
            ): Boolean {
                return oldItem == newItem
            }

        }
    ) {

    /**
     * Bind data to the holder.
     * Will set the content to display in the RecyclerView.
     */
    override fun onBindViewHolder(holder: PublisherHolder, position: Int) {
        val publisher = getItem(position)!!

        with(holder) {

            // Add data object.
            this.publisherEntity = publisher

            // Load image in background.
            ImagesLoader(
                publisher.imageFileURL,
                WeakReference(publisherImage),
                WeakReference(imageProgress)
            ).execute()

            // Fill TextViews.
            publisherName.text = publisher.name
            publisherVolumesCount.text =
                Localization.getLocalizedString(
                    R.string.volumes_number,
                    publisher.volumesCount
                )
        }
    }

    /**
     * Initialize the holder with a layout.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublisherHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.publisher_card, parent, false)
        return PublisherHolder(fragmentManager, activity, view)
    }

    /**
     * Holder for PublisherEntity datasets.
     * Organizes the publishers data in a CardView.
     */
    class PublisherHolder(
        fragmentManager: FragmentManager,
        activity: FragmentedActivity,
        itemView: View
    ) :
        RecyclerView.ViewHolder(itemView) {

        // Data object
        var publisherEntity: PublisherEntity? = null

        // UI elements.
        private val publisherCard: CardView = itemView.findViewById(R.id.publisherCard)
        val imageProgress : ProgressBar
        val publisherImage: ImageView
        val publisherName: TextView
        val publisherVolumesCount: TextView
        private val menuToggle: TextView

        init {
            imageProgress = publisherCard.findViewById(R.id.imageProgress)
            publisherImage = publisherCard.findViewById(R.id.publisherImage)
            publisherName = publisherCard.findViewById(R.id.publisherName)
            publisherVolumesCount = publisherCard.findViewById(R.id.publisherVolumesCount)
            menuToggle = publisherCard.findViewById(R.id.menuToggle)

            // Set OnClickListener on the card to navigate to the volumes of the publisher.
            publisherCard.setOnClickListener {

                // Show VolumesFragment.
                val fragment = VolumesFragment()
                val bundle = Bundle()
                bundle.putString(VolumesFragment.PUBLISHER_ID_NAME, publisherEntity?.id)
                fragment.arguments = bundle

                activity.replaceFragmentBackStack(fragment)
            }

            // Set OnClickListener on the menuToggle to show the popup menu.
            menuToggle.setOnClickListener {
                val menu =
                    PublisherPopupMenu(
                        itemView.context,
                        menuToggle,
                        publisherEntity,
                        fragmentManager
                    )
                menu.show()
            }
        }

    }
}