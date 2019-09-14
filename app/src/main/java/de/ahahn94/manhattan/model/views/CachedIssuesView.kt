package de.ahahn94.manhattan.model.views

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.PrimaryKey
import de.ahahn94.manhattan.api.responses.IssueReadStatus
import de.ahahn94.manhattan.model.entities.IssueEntity

/**
 * Data class for the CachedIssues database view.
 * The view combines the Issues table with the CachedComics table
 * for display in the app.
 */
@DatabaseView(
    value = "SELECT I.ID, I.Description, I.ImageFileURL, I.IssueNumber, I.Name, I.IsRead, I.CurrentPage, I.Changed, I.ReadStatusURL, I.VolumeID, CASE WHEN EXISTS (SELECT IssueID FROM CachedComics C WHERE C.IssueID = I.ID) THEN '1' ELSE '0' END AS IsCached, C.FileName, C.Readable, C.Unpacked, C.UnpackedDirectory FROM Issues I LEFT OUTER JOIN CachedComics C ON I.ID = C.IssueID",
    viewName = "CachedIssues"
)
data class CachedIssuesView(

    @ColumnInfo(name = "ID")
    @PrimaryKey
    @NonNull
    var id: String = "",

    @ColumnInfo(name = "Description")
    var description: String = "",

    @ColumnInfo(name = "ImageFileURL")
    var imageFileURL: String = "",

    @ColumnInfo(name = "IssueNumber")
    var number: String = "",

    @ColumnInfo(name = "Name")
    var name: String? = "",

    @Embedded
    var readStatus: IssueEntity.ReadStatus,

    @ColumnInfo(name = "VolumeID")
    val volumeID: String,

    @ColumnInfo(name = "IsCached")
    val isCached: String,

    @Embedded
    val cachedComic: CachedComic?
) {

    /**
     * Data class for the read-status part of an issue.
     */
    data class ReadStatus(
        @ColumnInfo(name = "IsRead")
        var isRead: String = IssueReadStatus.IS_READ_UNREAD,

        @ColumnInfo(name = "CurrentPage")
        var currentPage: String = IssueReadStatus.CURRENT_PAGE_NO_PROGRESS,

        @ColumnInfo(name = "Changed")
        var timestampChanged: String = ""
    )

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
        var unpacked: Boolean = false,

        // Directory the file has been unpacked to.
        @ColumnInfo(name = "UnpackedDirectory")
        var unpackedDirectory: String = ""
    )

}