package de.ahahn94.manhattan.model.entities

import androidx.annotation.NonNull
import androidx.room.*
import java.util.*

/**
 * Entity data class for cached comics on the database.
 */
@Entity(
    tableName = "CachedComics",
    foreignKeys = [ForeignKey(
        entity = IssueEntity::class,
        parentColumns = ["ID"],
        childColumns = ["IssueID"]
    )]
)
data class CachedComicEntity(

    @ColumnInfo(name = "IssueID")
    @PrimaryKey
    @NonNull
    var issueID: String = "",

    @ColumnInfo(name = "FileName")
    var fileName: String = "",

    // Can the file be unpacked for reading?
    @ColumnInfo(name = "Readable")
    var readable: Boolean = false,

    // Has the file been unpacked for reading?
    @ColumnInfo(name = "Unpacked")
    var unpacked: Boolean = false
) {

    companion object {

        // Constants.
        private val readableFormats = arrayOf("cbr", "cbz") // List of the file formats the app can open.

        /**
         * Check if a file can be unpacked for reading inside the app.
         * True if the file can be unpacked, else false.
         */
        fun isReadable(fileName: String): Boolean {
            val extension = fileName.split(".").last().toLowerCase(Locale.getDefault())
            return readableFormats.contains(extension)
        }

    }

    /**
     * Data access object for the CachedComics database table.
     */
    @Dao
    interface CachedComicsDao {
        @Insert(onConflict = OnConflictStrategy.ABORT)
        fun insert(cachedComicEntity: CachedComicEntity)

        @Update
        fun update(vararg cachedComicEntity: CachedComicEntity)

        @Query("SELECT * FROM CachedComics")
        fun getAll(): Array<CachedComicEntity>

        @Query("SELECT * FROM CachedComics WHERE IssueID = :issueID")
        fun get(issueID: String): CachedComicEntity?

        @Delete
        fun delete(vararg cachedComicEntity: CachedComicEntity)
    }

}