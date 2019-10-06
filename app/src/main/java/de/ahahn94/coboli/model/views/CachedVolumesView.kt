package de.ahahn94.coboli.model.views

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.PrimaryKey

/**
 * Data class for the CachedVolumes database view.
 * The view combines the VolumeReadStatus view with the CachedIssues view
 * for display in the app.
 */
@DatabaseView(
    value = "SELECT V.ID, V.Description, V.ImageFileURL, V.Name, V.StartYear, V.IssueCount, V.IsRead, V.Changed, V.PublisherID, CASE WHEN EXISTS (SELECT VolumeID FROM (SELECT VolumeID FROM CachedIssues WHERE IsCached = '1') CI WHERE CI.VolumeID = V.ID) THEN '1' ELSE '0' END AS HasCachedIssues FROM VolumesView V",
    viewName = "CachedVolumes"
)
data class CachedVolumesView(

    @ColumnInfo(name = "ID")
    @PrimaryKey
    @NonNull
    var id: String = "",

    @ColumnInfo(name = "Description")
    var description: String = "",

    @ColumnInfo(name = "ImageFileURL")
    var imageFileURL: String = "",

    @ColumnInfo(name = "Name")
    var name: String = "",

    @ColumnInfo(name = "StartYear")
    var startYear: String = "",

    @ColumnInfo(name = "IssueCount")
    var issueCount: Int,

    @Embedded
    var readStatus: ReadStatus? = null,

    @ColumnInfo(name = "PublisherID")
    var publisherID: String,

    @ColumnInfo(name = "HasCachedIssues")
    var hasCachedIssues: String
) {

    /**
     * Data class for the ReadStatus part of CachedVolumesView.
     */
    data class ReadStatus(
        @ColumnInfo(name = "IsRead")
        var isRead: String,

        @ColumnInfo(name = "Changed")
        var timestampChanged: String
    )

}