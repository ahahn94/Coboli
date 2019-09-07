package de.ahahn94.manhattan.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import de.ahahn94.manhattan.cache.ImagesCache
import java.lang.ref.WeakReference

/**
 * AsyncTask for loading images in the background.
 * Loads the cached copy of the image file specified by imageFileURL
 * into a bitmap and sets it on imageView.
 */
class ImagesLoader(
    private val imageFileURL: String,
    private val imageView: WeakReference<ImageView>
) : AsyncTask<Unit, Int, Bitmap>() {

    override fun doInBackground(vararg params: Unit?): Bitmap? {
        return BitmapFactory.decodeFile(ImagesCache.getImageFilePath(imageFileURL))
    }

    override fun onPostExecute(bitmap: Bitmap) {
        imageView.get()?.setImageBitmap(bitmap)
    }

}