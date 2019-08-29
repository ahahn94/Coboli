package de.ahahn94.manhattan.database

import androidx.annotation.NonNull
import androidx.room.*
import com.google.gson.annotations.SerializedName
import de.ahahn94.manhattan.api.responses.VolumeReadStatus

/**
 * Data class for volume datasets.
 */
@Entity(
    tableName = "Volumes",
    foreignKeys = [ForeignKey(
        entity = Publisher::class,
        parentColumns = ["ID"],
        childColumns = ["PublisherID"]
    )],
    indices = [Index(value = ["PublisherID"], unique = false)]
)
data class Volume(

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

    @SerializedName("Name")
    @ColumnInfo(name = "Name")
    var name: String = "",

    @SerializedName("StartYear")
    @ColumnInfo(name = "StartYear")
    var startYear: String = "",

    @SerializedName("IssuesURL")
    @ColumnInfo(name = "IssuesURL")
    var issuesURL: String = "",

    @SerializedName("IssuesCount")
    @Ignore
    var issuesCount: Int = 0,

    @SerializedName("ReadStatus")
    @Embedded
    var readStatus: ReadStatus = ReadStatus(),

    @SerializedName("Publisher")
    @Embedded
    var publisher: Publisher = Publisher()

) {

    /**
     * Data class for the read-status part of Volume.
     */
    data class ReadStatus(
        @SerializedName("IsRead")
        @ColumnInfo(name = "IsRead")
        var isRead: String = VolumeReadStatus.IS_READ_UNREAD,

        @SerializedName("Changed")
        @ColumnInfo(name = "Changed")
        var timestampChanged: String = "",

        @SerializedName("Link")
        @ColumnInfo(name = "ReadStatusURL")
        var link: String = ""
    )

    /**
     * Data class for the publisher part of Volume.
     */
    data class Publisher(
        @SerializedName("PublisherID")
        @ColumnInfo(name = "PublisherID")
        @NonNull
        var publisherID: String = "",

        @SerializedName("Link")
        @ColumnInfo(name = "PublisherURL")
        var link: String = ""
    )

    /**
     * Data access object for the Volumes database table.
     */
    @Dao
    interface VolumesDao {

        @Insert(onConflict = OnConflictStrategy.ABORT)
        fun insert(volume: Volume)

        @Update
        fun update(vararg volume: Volume)

        @Query("SELECT * FROM Volumes WHERE ID = :volumeID")
        fun get(volumeID: String): Volume?

        @Query("SELECT * FROM Volumes")
        fun getAll(): Array<Volume>

        @Query("SELECT * FROM Volumes WHERE PublisherID = :publisherID")
        fun getByPublisher(publisherID: String): Array<Volume>

        @Delete
        fun delete(vararg volume: Volume)

    }

}