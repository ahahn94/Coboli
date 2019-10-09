package de.ahahn94.coboli.model.views

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.PrimaryKey
import de.ahahn94.coboli.api.responses.IssueReadStatus
import de.ahahn94.coboli.model.entities.CachedComicEntity
import java.io.Serializable

/**
 * Data class for the CachedIssues database view.
 * The view combines the Issues table with the CachedComics table
 * for display in the app.
 */
@DatabaseView(
    value = "SELECT I.ID, I.Description, I.ImageFileURL, I.IssueNumber, I.Name, I.IsRead, I.CurrentPage, I.Changed, I.ReadStatusURL, I.VolumeID, CASE WHEN EXISTS (SELECT IssueID FROM CachedComics C WHERE C.IssueID = I.ID) THEN 1 ELSE 0 END AS IsCached, C.FileName, C.Readable, C.Unpacked, (SELECT V.Name FROM Volumes V WHERE I.VolumeID = V.ID) AS VolumeName FROM Issues I LEFT OUTER JOIN CachedComics C ON I.ID = C.IssueID",
    viewName = "CachedIssues"
)
data class CachedIssuesView(

    @ColumnInfo(name = "ID")
    @PrimaryKey
    @NonNull
    var id: String = "",

    @ColumnInfo(name = "VolumeName")
    var volumeName: String = "",

    @ColumnInfo(name = "Description")
    var description: String = "",

    @ColumnInfo(name = "ImageFileURL")
    var imageFileURL: String = "",

    @ColumnInfo(name = "IssueNumber")
    var number: String = "",

    @ColumnInfo(name = "Name")
    var name: String? = "",

    @Embedded
    var readStatus: ReadStatus,

    @ColumnInfo(name = "VolumeID")
    val volumeID: String,

    @ColumnInfo(name = "IsCached")
    val isCached: Boolean,

    @Embedded
    val cachedComic: CachedComic?
) : Serializable {

    val cachedComicEntity: CachedComicEntity?
        get() {
            return if (cachedComic != null) {

                CachedComicEntity(
                    id,
                    cachedComic.fileName,
                    cachedComic.readable,
                    cachedComic.unpacked
                )
            } else null
        }

    /**
     * Data class for the read-status part of an issue.
     */
    data class ReadStatus(
        @ColumnInfo(name = "IsRead")
        var isRead: Boolean = false,

        @ColumnInfo(name = "CurrentPage")
        var currentPage: Int = 0,

        @ColumnInfo(name = "Changed")
        var timestampChanged: String = ""
    ) : Serializable

    /**
     * Data class for the cached comic of an issue.
     */
    data class CachedComic(

        @ColumnInfo(name = "FileName")
        var fileName: String = "",

        // Can the file be unpacked for reading?
        @ColumnInfo(name = "Readable")
        var readable: Boolean = false,

        // Has the file been unpacked for reading?
        @ColumnInfo(name = "Unpacked")
        var unpacked: Boolean = false
    ) : Serializable

}