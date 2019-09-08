package de.ahahn94.manhattan.model.entities

import androidx.annotation.NonNull
import androidx.room.*
import com.google.gson.annotations.SerializedName
import de.ahahn94.manhattan.api.responses.IssueReadStatus

/**
 * Entity data class for issue datasets.
 */
@Entity(
    tableName = "Issues",
    foreignKeys = [ForeignKey(
        entity = VolumeEntity::class,
        parentColumns = ["ID"],
        childColumns = ["VolumeID"]
    )],
    indices = [Index(value = ["VolumeID"], unique = false)]
)
data class IssueEntity(

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
    var name: String? = "",

    @SerializedName("ReadStatus")
    @Embedded
    var readStatus: ReadStatus,

    @SerializedName("Volume")
    @Embedded
    val volume: Volume

) {

    /**
     * Data class for the comic file part of the IssueEntity.
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
     * Data class for the read-status part of the IssueEntity.
     */
    data class ReadStatus(
        @SerializedName("IsRead")
        @ColumnInfo(name = "IsRead")
        var isRead: String = IssueReadStatus.IS_READ_UNREAD,

        @SerializedName("CurrentPage")
        @ColumnInfo(name = "CurrentPage")
        var currentPage: String = IssueReadStatus.CURRENT_PAGE_NO_PROGRESS,

        @SerializedName("Changed")
        @ColumnInfo(name = "Changed")
        var timestampChanged: String = "",

        @SerializedName("Link")
        @ColumnInfo(name = "ReadStatusURL")
        var link: String = ""
    )

    /**
     * Data class for the volume part of the IssueEntity.
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
        fun insert(issueEntity: IssueEntity)

        @Update
        fun update(vararg issueEntity: IssueEntity)

        @Query("SELECT * FROM Issues WHERE ID = :issueID")
        fun get(issueID: String): IssueEntity?

        @Query("SELECT * FROM Issues")
        fun getAll(): Array<IssueEntity>

        @Query("SELECT * FROM Issues WHERE VolumeID = :volumeID")
        fun getByVolume(volumeID: String): Array<IssueEntity>

        @Delete
        fun delete(vararg issueEntity: IssueEntity)
    }

}