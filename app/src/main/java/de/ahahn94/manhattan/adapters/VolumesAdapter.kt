package de.ahahn94.manhattan.adapters

import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.activities.IssuesActivity
import de.ahahn94.manhattan.menus.VolumePopupMenu
import de.ahahn94.manhattan.model.entities.VolumeEntity
import de.ahahn94.manhattan.utils.Localization
import de.ahahn94.manhattan.utils.Logging
import java.lang.ref.WeakReference

/**
 * PagedListAdapter for VolumeEntity datasets.
 * Provides the data for a RecyclerView to display.
 */
class VolumesAdapter(
    private val fragmentManager: WeakReference<FragmentManager>,
    val volumes: LiveData<PagedList<VolumeEntity>>
) :
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
            } else {
                isReadBadge.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Initialize the holder with a layout.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolumeDatasetHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.volume_card, parent, false)
        return VolumeDatasetHolder(fragmentManager, view)
    }

    /**
     * Holder for VolumeEntity datasets.
     * Organizes the volumes data in a CardView.
     */
    class VolumeDatasetHolder(fragmentManager: WeakReference<FragmentManager>, itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        // Data object
        var volumeEntity: VolumeEntity? = null

        // UI elements.
        private val volumeCard: CardView = itemView.findViewById(R.id.volumeCard)
        val volumeImage: ImageView
        val volumeName: TextView
        val volumeIssuesCount: TextView
        val isReadBadge: TextView
        private val menuToggle: TextView

        init {
            volumeImage = volumeCard.findViewById(R.id.volumeImage)
            volumeName = volumeCard.findViewById(R.id.volumeName)
            volumeIssuesCount = volumeCard.findViewById(R.id.volumeIssuesCount)
            isReadBadge = volumeCard.findViewById(R.id.isReadBadge)
            menuToggle = volumeCard.findViewById(R.id.menuToggle)

            // Set OnClickListener on the card to navigate to the issues of the volume.
            volumeCard.setOnClickListener {
                val intent = Intent(itemView.context, IssuesActivity::class.java)
                intent.putExtra(IssuesActivity.VOLUME_ID_NAME, volumeEntity?.id)
                itemView.context.startActivity(intent)
            }

            // Set OnClickListener on the menuToggle to show the popup menu.
            menuToggle.setOnClickListener {
                val menu =
                    VolumePopupMenu(
                        itemView.context,
                        menuToggle,
                        Gravity.END,
                        volumeEntity,
                        fragmentManager
                    )
                menu.show()
            }

        }

    }
}