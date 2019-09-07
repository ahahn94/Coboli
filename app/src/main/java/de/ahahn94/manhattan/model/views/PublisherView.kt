package de.ahahn94.manhattan.model.views

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.PrimaryKey

/**
 * Data class for the publisher view.
 * As the PublisherVolume contains a field that has to be computed (VolumesCount),
 * reading from the database requires a complex SELECT statement.
 * To keep the SELECT statements on the PublishersDao simple, it is a good idea to use a view for the
 * complex stuff.
 */
@DatabaseView(
    value = "SELECT P.ID, P.URL, P.Description, P.ImageFileURL, P.Name, P.VolumesURL, COUNT (DISTINCT V.ID) AS VolumesCount FROM Publishers P LEFT OUTER JOIN Volumes V ON P.ID = V.PublisherID GROUP BY P.ID",
    viewName = "PublisherView"
)
data class PublisherView(

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

    @ColumnInfo(name = "VolumesURL")
    var volumesURL: String = "",

    @ColumnInfo(name = "VolumesCount")
    var volumesCount: Int = 0

)