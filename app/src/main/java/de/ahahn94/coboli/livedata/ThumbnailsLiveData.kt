package de.ahahn94.coboli.livedata

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.lifecycle.LiveData
import de.ahahn94.coboli.R
import de.ahahn94.coboli.model.views.CachedIssuesView
import de.ahahn94.coboli.utils.ContextProvider

/**
 * Class to provide an CachedIssuesView and the bitmaps
 * of the thumbnails of the issues pages on the cache as LiveData.
 */
class ThumbnailsLiveData(
    val issue: CachedIssuesView
) : LiveData<ArrayList<Bitmap>>() {

    // Internal list of the filepaths.
    private val list: ArrayList<Bitmap> = arrayListOf()

    // Dummy bitmap for use as a placeholder.
    val dummy = loadDummy()!!

    /**
     * Add an item to the list and trigger the LiveData to notify
     * all Observers about the change.
     */
    fun add(bitmap: Bitmap) {
        list.add(bitmap)
        postValue(list)
    }

    /**
     * Set an item on the list and trigger the LiveData to notify
     * all Observers about the change.
     */
    fun set(position: Int, bitmap: Bitmap) {
        list[position] = bitmap
        postValue(list)
    }

    /**
     * Load the dummy.
     * Uses the app icon as dummy bitmap.
     */
    private fun loadDummy(): Bitmap? {
        val drawable =
            ContextProvider.getApplicationContext().getDrawable(R.mipmap.ic_coboli_round)!!

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}