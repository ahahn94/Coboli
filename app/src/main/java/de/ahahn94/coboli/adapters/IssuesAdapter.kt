package de.ahahn94.coboli.adapters

import android.view.Gravity
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
import de.ahahn94.coboli.R
import de.ahahn94.coboli.asynctasks.ImagesLoader
import de.ahahn94.coboli.menus.IssuePopupMenu
import de.ahahn94.coboli.model.views.CachedIssuesView
import java.lang.ref.WeakReference

/**
 * PagedListAdapter for CachedIssuesView datasets.
 * Provides the data for a RecyclerView to display.
 */
class IssuesAdapter(
    private val fragmentManager: WeakReference<FragmentManager>,
    val issues: LiveData<PagedList<CachedIssuesView>>
) :
    PagedListAdapter<CachedIssuesView, IssuesAdapter.IssueDatasetHolder>(

        object : DiffUtil.ItemCallback<CachedIssuesView>() {
            override fun areItemsTheSame(
                oldItem: CachedIssuesView,
                newItem: CachedIssuesView
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CachedIssuesView,
                newItem: CachedIssuesView
            ): Boolean {
                return oldItem == newItem
            }

        }
    ) {

    /**
     * Bind data to the holder.
     * Will set the content to display in the RecyclerView.
     */
    override fun onBindViewHolder(holder: IssueDatasetHolder, position: Int) {
        val issue = getItem(position)!!

        with(holder) {

            // Add data object.
            this.issue = issue

            // Load image in background.
            ImagesLoader(
                issue.imageFileURL,
                WeakReference(issueImage),
                WeakReference(imageProgress)
            ).execute()

            // Fill TextViews.
            issueName.text = issue.name

            // Set isRead badge visibility.
            if (issue.readStatus.isRead) {
                isReadBadge.visibility = View.INVISIBLE
            } else {
                isReadBadge.visibility = View.VISIBLE
            }

            // Set badge visibility.
            if (issue.isCached) {
                isCloudBadge.visibility = View.INVISIBLE
            } else {
                isCloudBadge.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Initialize the holder with a layout.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueDatasetHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_card_issue, parent, false)
        return IssueDatasetHolder(fragmentManager, view)
    }

    /**
     * Holder for CachedIssuesView datasets.
     * Organizes the issues data in a CardView.
     */
    class IssueDatasetHolder(fragmentManager: WeakReference<FragmentManager>, itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        // Data object
        var issue: CachedIssuesView? = null

        // UI elements.
        private val issueCard: CardView = itemView.findViewById(R.id.card_issue)
        val issueImage: ImageView
        val imageProgress : ProgressBar
        val issueName: TextView
        val isReadBadge: TextView
        val isCloudBadge: TextView
        private val menuToggle: TextView

        init {
            imageProgress = issueCard.findViewById(R.id.progress_image)
            issueImage = issueCard.findViewById(R.id.image_cover)
            issueName = issueCard.findViewById(R.id.label_name)
            isReadBadge = issueCard.findViewById(R.id.badge_is_read)
            isCloudBadge = issueCard.findViewById(R.id.badge_is_cloud)
            menuToggle = issueCard.findViewById(R.id.button_menu)

            // Set OnClickListener on the menuToggle to show the popup menu.
            menuToggle.setOnClickListener {
                val menu =
                    IssuePopupMenu(
                        itemView.context,
                        menuToggle,
                        Gravity.END,
                        issue,
                        fragmentManager
                    )
                menu.show()
            }

        }

    }
}