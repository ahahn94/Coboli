package de.ahahn94.manhattan.asynctasks

import android.graphics.BitmapFactory
import android.os.AsyncTask
import de.ahahn94.manhattan.cache.ComicsCache
import de.ahahn94.manhattan.livedata.ThumbnailsLiveData

/**
 * AsyncTask for loading page thumbnails in the background.
 * Loads the cached page files specified by pageFilePath
 * into bitmaps while scaling them down to thumbnails.
 */
class ThumbnailsLoader(
    private val thumbnails: ThumbnailsLiveData
) : AsyncTask<Unit, Int, Unit>() {

    override fun doInBackground(vararg params: Unit?) {

        // Get files list.
        val files = ComicsCache.getExtractedComic(thumbnails.issue.id)

        // Fill thumbnails with dummys.
        repeat(files.size) {
            thumbnails.add(thumbnails.dummy)
        }

        // Prepare options for scaling down thumbnails.
        val options = BitmapFactory.Options()
        options.inSampleSize = 10   // Lower values lead to longer loading times and bigger bitmaps.

        // Load bitmaps. Stop immediately if task is cancelled.
        val iterator = files.iterator().withIndex()
        while (!isCancelled && iterator.hasNext()) {
            val it = iterator.next()
            val bitmap = BitmapFactory.decodeFile(it.value, options)
            thumbnails.set(it.index, bitmap)
        }

    }
}