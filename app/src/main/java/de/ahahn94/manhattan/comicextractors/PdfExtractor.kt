package de.ahahn94.manhattan.comicextractors

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import de.ahahn94.manhattan.cache.ComicsCache
import de.ahahn94.manhattan.model.entities.CachedComicEntity
import de.ahahn94.manhattan.utils.ContextProvider
import java.io.ByteArrayOutputStream
import java.io.File


/**
 * ComicExtractor to unpack PDF files.
 */
class PdfExtractor {

    companion object : ComicExtractor {

        /**
         * Extract the comic file specified by fileName to the parentFolder.
         */
        @Throws(ExtractorException::class)
        override fun extract(fileName: String, parentFolder: File) {
            val file = ComicsCache.getFile(fileName)
            if (file != null) {
                if (CachedComicEntity.isReadable(file.name)) {

                    // create a new renderer
                    val renderer = PdfRenderer(
                        ParcelFileDescriptor.open(
                            file,
                            ParcelFileDescriptor.MODE_READ_ONLY
                        )
                    )

                    // Calculate padding for the filenames.
                    val padding = renderer.pageCount.toString().length

                    // Get width for landscape mode.
                    val width =
                        with(ContextProvider.getApplicationContext().resources.displayMetrics) {
                            if (widthPixels > heightPixels) widthPixels else heightPixels
                        }

                    // Render pages and save resulting bitmaps to files.
                    with(renderer) {
                        for (i in 0 until pageCount) {
                            val page = openPage(i)

                            // Scale height and width of the bitmap.
                            // The width in landscape mode is the widest available space for the
                            // image, so scaling pages down to that.
                            val ratio = page.width.toDouble() / page.height.toDouble()
                            val height = (width / ratio).toInt()

                            // Get filename and render content from PDF page.
                            val name = "${i.toString().padStart(padding, '0')}.png"
                            val bitmap =
                                Bitmap.createBitmap(
                                    width,
                                    height,
                                    Bitmap.Config.ARGB_8888
                                )
                            page.render(
                                bitmap,
                                null,
                                null,
                                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                            )

                            // Save bitmap to file.
                            val imageFile = File(parentFolder, name)
                            if (!imageFile.exists()) {
                                val outputStream = ByteArrayOutputStream()
                                bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
                                imageFile.writeBytes(outputStream.toByteArray())
                            }

                            // Close the page.
                            page.close()
                        }
                    }

                    // close the renderer
                    renderer.close()
                }
            }
        }
    }
}