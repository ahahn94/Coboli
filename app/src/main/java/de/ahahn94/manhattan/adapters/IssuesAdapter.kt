package de.ahahn94.manhattan.adapters

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
import de.ahahn94.manhattan.menus.IssuePopupMenu
import de.ahahn94.manhattan.model.entities.IssueEntity
import java.lang.ref.WeakReference

/**
 * PagedListAdapter for IssueEntity datasets.
 * Provides the data for a RecyclerView to display.
 */
class IssuesAdapter(
    private val fragmentManager: WeakReference<FragmentManager>,
    val issues: LiveData<PagedList<IssueEntity>>
) :
    PagedListAdapter<IssueEntity, IssuesAdapter.IssueDatasetHolder>(

        object : DiffUtil.ItemCallback<IssueEntity>() {
            override fun areItemsTheSame(
                oldItem: IssueEntity,
                newItem: IssueEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: IssueEntity,
                newItem: IssueEntity
            ): Boolean {
                return oldItem.equals(newItem)
            }

        }
    ) {

    /**
     * Bind data to the holder.
     * Will set the content to display in the RecyclerView.
     */
    override fun onBindViewHolder(holder: IssueDatasetHolder, position: Int) {
        val issueEntity = getItem(position)!!

        with(holder) {

            // Add data object.
            this.issueEntity = issueEntity

            // Load image in background.
            ImagesLoader(
                issueEntity.imageFileURL,
                WeakReference(issueImage)
            ).execute()

            // Fill TextViews.
            issueName.text = issueEntity.name

            // Set badge visibility.
            if (issueEntity.readStatus.isRead == "1") {
                isReadBadge.visibility = View.INVISIBLE
            } else {
                isReadBadge.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Initialize the holder with a layout.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueDatasetHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.issue_card, parent, false)
        return IssueDatasetHolder(fragmentManager, view)
    }

    /**
     * Holder for IssueEntity datasets.
     * Organizes the issues data in a CardView.
     */
    class IssueDatasetHolder(fragmentManager: WeakReference<FragmentManager>, itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        // Data object
        var issueEntity: IssueEntity? = null

        // UI elements.
        private val issueCard: CardView = itemView.findViewById(R.id.issueCard)
        val issueImage: ImageView
        val issueName: TextView
        val isReadBadge: TextView
        private val menuToggle: TextView

        init {
            issueImage = issueCard.findViewById(R.id.issueImage)
            issueName = issueCard.findViewById(R.id.issueName)
            isReadBadge = issueCard.findViewById(R.id.isReadBadge)
            menuToggle = issueCard.findViewById(R.id.menuToggle)

            // Set OnClickListener on the menuToggle to show the popup menu.
            menuToggle.setOnClickListener {
                val menu =
                    IssuePopupMenu(
                        itemView.context,
                        menuToggle,
                        Gravity.END,
                        issueEntity,
                        fragmentManager
                    )
                menu.show()
            }

        }

    }
}