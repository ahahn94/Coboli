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
import de.ahahn94.manhattan.model.entities.VolumeEntity
import de.ahahn94.manhattan.utils.Localization
import java.lang.ref.WeakReference

/**
 * PagedListAdapter for VolumeEntity datasets.
 * Provides the data for a RecyclerView to display.
 */
class VolumesAdapter(val volumes: LiveData<PagedList<VolumeEntity>>) :
    PagedListAdapter<VolumeEntity, VolumesAdapter.VolumeDatasetHolder>(

        object : DiffUtil.ItemCallback<VolumeEntity>() {
            override fun areItemsTheSame(
                oldItem: VolumeEntity,
                newItem: VolumeEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: VolumeEntity,
                newItem: VolumeEntity
            ): Boolean {
                return oldItem.equals(newItem)
            }

        }
    ) {

    /**
     * Bind data to the holder.
     * Will set the content to display in the RecyclerView.
     */
    override fun onBindViewHolder(holder: VolumeDatasetHolder, position: Int) {
        val volumeEntity = getItem(position)!!

        with(holder) {

            // Add data object.
            this.volumeEntity = volumeEntity

            // Load image in background.
            ImagesLoader(
                volumeEntity.imageFileURL,
                WeakReference(volumeImage)
            ).execute()

            // Fill TextViews.
            volumeName.text = volumeEntity.name
            volumeIssuesCount.text =
                Localization.getLocalizedString(
                    R.string.issues_number,
                    volumeEntity.issueCount
                )

            // Set badge visibility.
            if (volumeEntity.readStatus?.isRead == "1") {
                isReadBadge.visibility = View.INVISIBLE
            }
        }
    }

    /**
     * Initialize the holder with a layout.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolumeDatasetHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.volume_card_layout, parent, false)
        return VolumeDatasetHolder(view)
    }

    /**
     * Holder for VolumeEntity datasets.
     * Organizes the volumes data in a CardView.
     */
    class VolumeDatasetHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Data object
        var volumeEntity: VolumeEntity? = null

        // UI elements.
        private val volumeCard: CardView = itemView.findViewById(R.id.volumeCard)
        val volumeImage: ImageView
        val volumeName: TextView
        val volumeIssuesCount: TextView
        val isReadBadge: TextView

        init {
            volumeImage = volumeCard.findViewById(R.id.volumeImage)
            volumeName = volumeCard.findViewById(R.id.volumeName)
            volumeIssuesCount = volumeCard.findViewById(R.id.volumeIssuesCount)
            isReadBadge = volumeCard.findViewById(R.id.isReadBadge)

            // Todo: Clickevents.
            volumeCard.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    Toast.makeText(
                        itemView.context,
                        volumeEntity?.name.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }

            })
        }

    }
}