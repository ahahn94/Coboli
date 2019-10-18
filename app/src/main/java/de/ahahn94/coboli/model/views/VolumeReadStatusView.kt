package de.ahahn94.coboli.model.views

import androidx.room.ColumnInfo
import androidx.room.DatabaseView

/**
 * Data class for the read-status part of VolumeEntity.
 * VolumeReadStatusView is generated via a view that summarizes the issues of the volume into one status.
 * The "Changed" field is the date of the latest change to the volumes issue last changed.
 * The "IsRead" field is 1 if all of the volumes issues are read, else false.
 */
@DatabaseView(
    // Use COALESCE on Changed to prevent null-Exception if trying to get the VolumeReadStatusView before Issues for the VolumeEntity where added.
    // Results in a default dataset like VolumeReadStatusView=(VolumeID=$volumeID, IssuesCount=0, IsRead="0", Changed="").
    value = "SELECT V.ID AS VolumeID, COUNT(DISTINCT I.ID) AS VolumeIssueCount, CASE WHEN SUM(I.IsRead) = COUNT(DISTINCT I.ID) THEN '1' ELSE '0' END AS VolumeIsRead, COALESCE(MAX(DATETIME(I.Changed)), '') AS VolumeChanged FROM Volumes V LEFT OUTER JOIN Issues I ON I.VolumeID = V.ID GROUP BY V.ID",
    viewName = "VolumeReadStatus"
)
data class VolumeReadStatusView(

    @ColumnInfo(name = "VolumeID")
    val volumeID: String,

    @ColumnInfo(name = "VolumeIssueCount")
    val issuesCount: Int,

    @ColumnInfo(name = "VolumeIsRead")
    var isRead: Boolean,

    @ColumnInfo(name = "VolumeChanged")
    var timestampChanged: String
)