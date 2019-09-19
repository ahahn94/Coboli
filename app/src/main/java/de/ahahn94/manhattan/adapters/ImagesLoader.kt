package de.ahahn94.manhattan.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import de.ahahn94.manhattan.cache.ImagesCache
import java.lang.ref.WeakReference

/**
 * AsyncTask for loading images in the background.
 * Loads the cached copy of the image file specified by imageFileURL
 * into a bitmap and sets it on imageView.
 */
class ImagesLoader(
    private val imageFileURL: String,
    private val imageView: WeakReference<ImageView>,
    private val progressBar: WeakReference<ProgressBar>
) : AsyncTask<Unit, Int, Bitmap>() {

    override fun doInBackground(vararg params: Unit?): Bitmap? {
        return BitmapFactory.decodeFile(ImagesCache.getImageFilePath(imageFileURL))
    }

    override fun onPostExecute(bitmap: Bitmap) {
        progressBar.get()?.visibility = View.GONE
        imageView.get()?.setImageBitmap(bitmap)
    }

}