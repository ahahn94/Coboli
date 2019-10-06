package de.ahahn94.coboli.model.entities

import androidx.annotation.NonNull
import androidx.paging.DataSource
import androidx.room.*
import com.google.gson.annotations.SerializedName
import de.ahahn94.coboli.model.views.CachedVolumesView

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

        @Query("SELECT * FROM VolumesView ORDER BY Name")
        fun getAll(): Array<VolumeEntity>

        @Query("SELECT * FROM VolumesView WHERE ID = :volumeID")
        fun get(volumeID: String): VolumeEntity?

        @Query("SELECT * FROM VolumesView WHERE PublisherID = :publisherID")
        fun getByPublisher(publisherID: String): Array<VolumeEntity>

        @Delete
        fun delete(vararg volumeEntity: VolumeEntity)

        @Query("Update Issues SET IsRead = :isRead, Changed = :changed WHERE VolumeID = :volumeID")
        fun updateReadStatus(volumeID: String, isRead: String, changed: String)

        /*
        Paged live data of CachedVolumesView for display in the apps activities.
         */

        @Query("SELECT * FROM CachedVolumes ORDER BY Name")
        fun getAllPaged(): DataSource.Factory<Int, CachedVolumesView>

        @Query("SELECT * FROM CachedVolumes WHERE HasCachedIssues = '1' ORDER BY Name")
        fun getCachedPaged(): DataSource.Factory<Int, CachedVolumesView>

        @Query("SELECT * FROM CachedVolumes WHERE PublisherID = :publisherID")
        fun getByPublisherPaged(publisherID: String): DataSource.Factory<Int, CachedVolumesView>

        @Query("SELECT * FROM CachedVolumes WHERE Name LIKE '%' || :query || '%'")
        fun getBySearchQueryPaged(query: String): DataSource.Factory<Int, CachedVolumesView>

    }

}