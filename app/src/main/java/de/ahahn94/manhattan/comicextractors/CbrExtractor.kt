package de.ahahn94.manhattan.comicextractors

import com.github.junrar.Archive
import com.github.junrar.exception.RarException
import de.ahahn94.manhattan.cache.ComicsCache
import de.ahahn94.manhattan.model.entities.CachedComicEntity
import de.ahahn94.manhattan.utils.FileTypes
import java.io.File

/**
 * ComicExtractor to unpack CBR files.
 */
class CbrExtractor {

    companion object : ComicExtractor {

        /**
         * Extract the comic file specified by fileName to the parentFolder.
         * May throw an ExtractorException if the file is encrypted or uses RAR version 5.
         */
        @Throws(ExtractorException::class)
        override fun extract(fileName: String, parentFolder: File) {
            val file = ComicsCache.getFile(fileName)
            if (file != null) {
                if (CachedComicEntity.isReadable(file.name)) {

                    try {
                        val rarFile = Archive(file.inputStream())

                        // Check if encrypted.
                        if (rarFile.isEncrypted) throw ExtractorException(
                            ExtractorException.Type.FILE_ENCRYPTED,
                            null
                        ) // No support for encryption. Show Toast.

                        // Get all entries.
                        val entries = rarFile.toList()

                        // Filter entries for images.
                        // Ignore files that are encrypted.
                        val filteredEntries = entries.filter {
                            !it.isDirectory && !it.isEncrypted && FileTypes.isImageFile(it.fileNameString)
                        }

                        // Save the image files to the parentFolder.
                        filteredEntries.forEach {
                            // FileHeader.fileNameString may contain a path. Reduce to filename.
                            val name = File(it.fileNameString).name

                            val imageFile = File(parentFolder, name)
                            if (!imageFile.exists()) {
                                val outputStream = imageFile.outputStream()
                                rarFile.extractFile(it, outputStream)
                                outputStream.close()
                            }
                        }

                        rarFile.close()
                    } catch (e: RarException) {
                        if (e.type == RarException.RarExceptionType.unsupportedRarArchive) {
                            // File uses RAR version 5. Not supported by junrar, so throw Exception
                            // which will show a Toast when getting back to the main thread.
                            throw ExtractorException(ExtractorException.Type.RAR_NOT_SUPPORTED, e)
                        } else {
                            throw e
                        }
                    }
                }
            }
        }
    }

}