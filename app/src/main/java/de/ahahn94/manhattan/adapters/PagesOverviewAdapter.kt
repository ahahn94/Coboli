package de.ahahn94.manhattan.adapters

import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.utils.Localization
import java.lang.ref.WeakReference

/**
 * ListAdapter for page thumbnails.
 * Provides the data for a RecyclerView to display.
 */
class PagesOverviewAdapter(
    private val currentPosition: Int,
    private val viewPager: WeakReference<ViewPager>,
    val fragment: DialogFragment
) :
    ListAdapter<Bitmap, PagesOverviewAdapter.PageHolder>(

        object : DiffUtil.ItemCallback<Bitmap>() {
            override fun areItemsTheSame(
                oldItem: Bitmap,
                newItem: Bitmap
            ): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(
                oldItem: Bitmap,
                newItem: Bitmap
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

        }
    ) {

    /**
     * Bind data to the holder.
     * Will set the content to display in the RecyclerView.
     */
    override fun onBindViewHolder(holder: PageHolder, position: Int) {
        val bitmap = getItem(position)
        with(holder) {

            // Add data.
            this.bitmap = bitmap
            number = position
            pageNumber.text =
                Localization.getLocalizedString(R.string.page_number, position + 1)

            pageImage.setImageBitmap(bitmap)

            // Set different card color and text color for current page.
            if (position == currentPosition) {
                cardFrame.setBackgroundColor(
                    ContextCompat.getColor(
                        fragment.context!!,
                        R.color.colorAccent
                    )
                )
                pageNumber.setTextColor(
                    ContextCompat.getColor(fragment.context!!, R.color.colorTextLight)
                )
            } else {
                cardFrame.setBackgroundColor(
                    ContextCompat.getColor(
                        fragment.context!!,
                        R.color.colorLight
                    )
                )
                pageNumber.setTextColor(
                    Color.BLACK
                )
            }

            // Jump to selected page.
            pageCard.setOnClickListener {
                viewPager.get()?.currentItem = position
                fragment.dismiss()
            }

        }
    }

    /**
     * Initialize the holder with a layout.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.page_card, parent, false)
        return PageHolder(view)
    }

    /**
     * Holder for page_cards.
     */
    class PageHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val pageCard: CardView = itemView.findViewById(R.id.PageCard)
        val cardFrame: FrameLayout = itemView.findViewById(R.id.CardFrame)
        val pageNumber: TextView = itemView.findViewById(R.id.PageNumber)
        val pageImage: ImageView = itemView.findViewById(R.id.PageImage)

        var bitmap: Bitmap? = null
        var number: Int = -1

    }
}