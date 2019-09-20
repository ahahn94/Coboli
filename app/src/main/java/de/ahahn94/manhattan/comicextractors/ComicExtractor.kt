package de.ahahn94.manhattan.comicextractors

import java.io.File

/**
 * Interface for unpacking comic files
 * to the comics cache.
 */
interface ComicExtractor {

    /**
     * Extract the comic file specified by fileName to the parentFolder.
     */
    @Throws(ExtractorException::class)
    fun extract(fileName: String, parentFolder: File)

}