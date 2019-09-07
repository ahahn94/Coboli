package de.ahahn94.manhattan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.model.entities.PublisherEntity
import de.ahahn94.manhattan.utils.Localization
import java.lang.ref.WeakReference

/**
 * PagedListAdapter for PublisherEntity datasets.
 * Provides the data for a RecyclerView to display.
 */
class PublishersAdapter(val publishers: LiveData<PagedList<PublisherEntity>>) :
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
                return oldItem.equals(newItem)
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
                WeakReference(publisherImage)
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
            .inflate(R.layout.publisher_card_layout, parent, false)
        return PublisherHolder(view)
    }

    /**
     * Holder for PublisherEntity datasets.
     * Organizes the publishers data in a CardView.
     */
    class PublisherHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Data object
        var publisherEntity: PublisherEntity? = null

        // UI elements.
        private val publisherCard: CardView = itemView.findViewById(R.id.publisherCard)
        val publisherImage: ImageView
        val publisherName: TextView
        val publisherVolumesCount: TextView

        init {
            publisherImage = publisherCard.findViewById(R.id.publisherImage)
            publisherName = publisherCard.findViewById(R.id.publisherName)
            publisherVolumesCount = publisherCard.findViewById(R.id.publisherVolumesCount)

            // Todo: Clickevents.
            publisherCard.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    Toast.makeText(
                        itemView.context,
                        publisherEntity?.name.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }

            })
        }

    }
}