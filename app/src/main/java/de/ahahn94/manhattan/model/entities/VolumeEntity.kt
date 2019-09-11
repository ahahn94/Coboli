package de.ahahn94.manhattan.model.entities

import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.room.*
import com.google.gson.annotations.SerializedName

/**
 * Entity data class for volume datasets.
 */
@Entity(
    tableName = "Volumes",
    foreignKeys = [ForeignKey(
        entity = PublisherEntity::class,
        parentColumns = ["ID"],
        childColumns = ["PublisherID"]
    )],
    indices = [Index(value = ["PublisherID"], unique = false)]
)
data class VolumeEntity(

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

    @SerializedName("IssueCount")
    @ColumnInfo(name = "IssueCount")
    var issueCount: Int,

    @SerializedName("ReadStatus")
    @Embedded
    var readStatus: ReadStatus? = null,

    @SerializedName("Publisher")
    @Embedded
    var publisher: Publisher = Publisher()

) {

    /**
     * Data class for the ReadStatus part of VolumeEntity.
     */
    data class ReadStatus(
        @SerializedName("IsRead")
        @ColumnInfo(name = "IsRead")
        var isRead: String,

        @SerializedName("Changed")
        @ColumnInfo(name = "Changed")
        var timestampChanged: String
    )

    /**
     * Data class for the publisher part of VolumeEntity.
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
     * Data access object for the VolumeEntity datasets.
     */
    @Dao
    interface VolumesDao {

        @Insert(onConflict = OnConflictStrategy.ABORT)
        fun insert(volumeEntity: VolumeEntity)

        @Update
        fun update(vararg volumeEntity: VolumeEntity)

        @Query("SELECT * FROM VolumeView ORDER BY Name")
        fun getAll(): Array<VolumeEntity>

        @Query("SELECT * FROM VolumeView WHERE ID = :volumeID")
        fun get(volumeID: String): VolumeEntity?

        @Query("SELECT * FROM VolumeView WHERE PublisherID = :publisherID")
        fun getByPublisher(publisherID: String): Array<VolumeEntity>

        @Delete
        fun delete(vararg volumeEntity: VolumeEntity)

        @Query("Update Issues SET IsRead = :isRead, Changed = :changed WHERE VolumeID = :volumeID")
        fun updateReadStatus(volumeID: String, isRead: String, changed: String)

        /*
        Paged live data.
         */

        @Query("SELECT * FROM VolumeView ORDER BY Name")
        fun getAllPaged(): androidx.paging.DataSource.Factory<Int, VolumeEntity>

        @Query("SELECT * FROM VolumeView WHERE PublisherID = :publisherID")
        fun getByPublisherPaged(publisherID: String): androidx.paging.DataSource.Factory<Int, VolumeEntity>

        @Query("SELECT * FROM VolumeView WHERE ID = :volumeID")
        fun getLiveData(volumeID: String): LiveData<VolumeEntity>

    }

}