package de.ahahn94.manhattan.database

import androidx.annotation.NonNull
import androidx.room.*
import com.google.gson.annotations.SerializedName
import de.ahahn94.manhattan.api.responses.IssueReadStatus

/**
 * Data class for issue datasets.
 */
@Entity(tableName = "Issues",
    foreignKeys = [ForeignKey(entity = Volume::class, parentColumns = ["ID"], childColumns = ["VolumeID"])])
data class Issue(

    @SerializedName("ID")
    @ColumnInfo(name = "ID")
    @PrimaryKey
    @NonNull
    var id: String = "",

    @SerializedName("Link")
    @ColumnInfo(name = "URL")
    var link: String = "",

    @SerializedName("Description")
    @ColumnInfo(name = "Description")
    var description: String = "",

    @SerializedName("ImageFileURL")
    @ColumnInfo(name = "ImageFileURL")
    var imageFileURL: String = "",

    @SerializedName("File")
    @Embedded
    val file: ComicFile,

    @SerializedName("Number")
    @ColumnInfo(name = "IssueNumber")
    var number: String = "",

    @SerializedName("Name")
    @ColumnInfo(name = "Name")
    var name: String = "",

    @SerializedName("ReadStatus")
    @Embedded
    val readStatus: ReadStatus,

    @SerializedName("Volume")
    @Embedded
    val volume: Volume

) {

    /**
     * Data class for the comic file part of the Issue.
     */
    data class ComicFile(
        @SerializedName("FileName")
        @ColumnInfo(name = "FileName")
        var fileName: String = "",

        @SerializedName("FileURL")
        @ColumnInfo(name = "FileURL")
        var fileURL: String = ""
    )

    /**
     * Data class for the read-status part of the Issue.
     */
    data class ReadStatus(
        @SerializedName("IsRead")
        @ColumnInfo(name = "IsRead")
        var isRead: String = IssueReadStatus.IS_READ_UNREAD,

        @SerializedName("CurrentPage")
        @ColumnInfo(name = "CurrentPage")
        var currentPage: String = IssueReadStatus.CURRENT_PAGE_NO_PROGRESS,

        @SerializedName("Link")
        @ColumnInfo(name = "ReadStatusURL")
        var link: String = ""
    )

    /**
     * Data class for the volume part of the Issue.
     */
    data class Volume(
        @SerializedName("VolumeID")
        @ColumnInfo(name = "VolumeID")
        @NonNull
        var volumeID: String = "",

        @SerializedName("Link")
        @ColumnInfo(name = "VolumeURL")
        var link: String = ""
    )

    /**
     * Data access object for the Issues database table.
     */
    @Dao
    interface IssuesDao {

        @Insert(onConflict = OnConflictStrategy.ABORT)
        fun insert(issue: Issue)

        @Query("SELECT * FROM Issues")
        fun getAll(): Array<Issue>
    }

}
