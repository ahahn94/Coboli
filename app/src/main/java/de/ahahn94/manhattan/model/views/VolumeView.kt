package de.ahahn94.manhattan.model.views

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.PrimaryKey

/**
 * Data class for the volume view.
 * As the VolumeEntity contains 3 fields that have to be computed (IssueCount, IsRead, Changed),
 * reading from the database requires a complex SELECT statement.
 * To keep the SELECT statements on the VolumesDao simple, it is a good idea to use a view for the
 * complex stuff.
 */
@DatabaseView(
    value = "SELECT V.*, VRS.VolumeIsRead AS IsRead, VRS.VolumeIssueCount AS IssueCount, VRS.VolumeChanged AS Changed FROM (SELECT ID, URL, Description, ImageFileURL, Name, StartYear, IssuesURL, PublisherID, PublisherURL FROM Volumes) V JOIN VolumeReadStatus VRS ON V.ID = VRS.VolumeID",
    viewName = "VolumeView"
)
data class VolumeView(

    @ColumnInfo(name = "ID")
    @PrimaryKey
    @NonNull
    var id: String = "",

    @ColumnInfo(name = "URL")
    var link: String = "",

    @ColumnInfo(name = "Description")
    var description: String = "",

    @ColumnInfo(name = "ImageFileURL")
    var imageFileURL: String = "",

    @ColumnInfo(name = "Name")
    var name: String = "",

    @ColumnInfo(name = "StartYear")
    var startYear: String = "",

    @ColumnInfo(name = "IssuesURL")
    var issuesURL: String = "",

    @ColumnInfo(name = "IssueCount")
    var issueCount: Int,

    @Embedded
    var readStatus: ReadStatus? = null,

    @Embedded
    var publisher: Publisher = Publisher()

) {

    /**
     * Data class for the ReadStatus part of VolumeEntity.
     */
    data class ReadStatus(
        @ColumnInfo(name = "IsRead")
        var isRead: String,

        @ColumnInfo(name = "Changed")
        var timestampChanged: String
    )

    /**
     * Data class for the publisher part of VolumeView.
     */
    data class Publisher(
        @ColumnInfo(name = "PublisherID")
        @NonNull
        var publisherID: String = "",

        @ColumnInfo(name = "PublisherURL")
        var link: String = ""
    )
}