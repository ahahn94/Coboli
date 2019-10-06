package de.ahahn94.coboli.comicextractors

import de.ahahn94.coboli.cache.ComicsCache
import de.ahahn94.coboli.model.entities.CachedComicEntity
import de.ahahn94.coboli.utils.FileTypes
import java.io.File
import java.util.zip.ZipFile

/**
 * ComicExtractor to unpack CBZ files.
 */
class CbzExtractor {

    companion object : ComicExtractor {

        /**
         * Extract the comic file specified by fileName to the parentFolder.
         */
        override fun extract(fileName: String, parentFolder: File) {
            val file = ComicsCache.getFile(fileName)
            if (file != null) {
                if (CachedComicEntity.isReadable(file.name)) {

                    val zipFile = ZipFile(file)
                    val entries = zipFile.entries()

                    // Filter entries for images.
                    val filteredEntries = entries.toList().filter {
                        !it.isDirectory && FileTypes.isImageFile(it.name)
                    }

                    // Save the image files to the parentFolder.
                    filteredEntries.forEach {

                        // ZipEntry.name may contain a path. Reduce to filename.
                        val name = File(it.name).name

                        val imageFile = File(parentFolder, name)
                        if (!imageFile.exists()) {
                            imageFile.writeBytes(
                                zipFile.getInputStream(it).readBytes()
                            )
                        }
                    }

                    zipFile.close()
                }
            }
        }
    }

}