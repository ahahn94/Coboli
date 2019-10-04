package de.ahahn94.manhattan.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import de.ahahn94.manhattan.R

/**
 * Adapter to provide comic pages to the ReaderActivity.
 */
class PagesAdapter(val context: Context, val menuFrame: FrameLayout, var list: List<String>) :
    PagerAdapter() {

    /**
     * Submit a changed list to the adapter.
     * Use this inside an Observer to update
     * the adapters content from LiveData.
     */
    fun submitList(list: List<String>) {
        this.list = list
        notifyDataSetChanged()
    }

    /**
     * Mandatory isViewFromObject-function.
     * Default implementation, nothing special.
     */
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    /**
     * Mandatory getCount-function.
     * Default implementation, nothing special.
     */
    override fun getCount(): Int {
        return list.size
    }

    /**
     * Instantiate a new item on the adapter.
     * Inflates the layout for the page, gets a bitmap from the file
     * referenced by the String at position and sets the bitmap on
     * the imageView.
     */
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val bitmap = BitmapFactory.decodeFile(list[position])
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_comic_page, null)
        val imageView = view.findViewById<ImageView>(R.id.image_page)
        imageView.setImageBitmap(bitmap)
        container.addView(view)
        imageView.setOnClickListener {
            // On click on a page, show/hide the menu overlay.
            when (menuFrame.visibility) {
                View.VISIBLE -> menuFrame.visibility = View.INVISIBLE
                View.INVISIBLE -> menuFrame.visibility = View.VISIBLE
            }
        }
        return view
    }

    /**
     * Remove a view from the adapter.
     * The adapter only keeps the current item plus one left and right of it.
     */
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        // Do not use removeViewAt. Makes the current page vanish after a few slides.
        container.removeView(view)
    }

}