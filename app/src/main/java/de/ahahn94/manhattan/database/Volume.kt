package de.ahahn94.manhattan.database

import androidx.annotation.NonNull
import androidx.room.*
import com.google.gson.annotations.SerializedName

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

    // Ignore this field in JSON de-/serialization and database.
    // Will be filled with data from the VolumeReadStatus view when using VolumesDao.get/getAll.
    @SerializedName("ReadStatus")
    @Transient
    @Ignore
    var readStatus: ReadStatus? = null,

    @SerializedName("Publisher")
    @Embedded
    var publisher: Publisher = Publisher()

) {

    /**
     * Data class for the read-status part of Volume.
     * ReadStatus is generated via a view that summarizes the issues of the volume into one status.
     * The "Changed" field is the date of the latest change to the volumes issue last changed.
     * The "IsRead" field is 1 if all of the volumes issues are read, else false.
     */
    @DatabaseView(
        // Use COALESCE on Changed to prevent null-Exception if trying to get the ReadStatus before Issues for the Volume where added.
        // Results in a default dataset like ReadStatus=(VolumeID=$volumeID, IssuesCount=0, IsRead="0", Changed="").
        value = "SELECT V.ID AS VolumeID, COUNT(DISTINCT I.ID) AS IssuesCount, CASE WHEN SUM(I.IsRead) = COUNT(DISTINCT I.ID) THEN '1' ELSE '0' END AS IsRead, COALESCE(MAX(DATETIME(I.Changed)), '') AS Changed FROM Volumes AS V LEFT OUTER JOIN Issues AS I ON I.VolumeID = V.ID GROUP BY V.ID",
        viewName = "VolumeReadStatus"
    )
    data class ReadStatus(

        @ColumnInfo(name = "VolumeID")
        val volumeID: String,

        @ColumnInfo(name = "IssuesCount")
        val issuesCount: Int,

        @ColumnInfo(name = "IsRead")
        var isRead: String,

        @ColumnInfo(name = "Changed")
        var timestampChanged: String
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

        /**
         * Get a Volume and its ReadStatus from the database.
         */
        @Transaction
        fun get(volumeID: String): Volume? {
            val volume = getVolume(volumeID)
            volume?.readStatus = getReadStatus(volumeID)
            return volume
        }

        @Query("SELECT * FROM Volumes WHERE ID = :volumeID")
        fun getVolume(volumeID: String): Volume?

        @Query("SELECT * FROM Volumes")
        fun getAllVolumes(): Array<Volume>

        @Query("SELECT * FROM VolumeReadStatus WHERE VolumeID = :volumeID")
        fun getReadStatus(volumeID: String): ReadStatus?

        /**
         * Get all Volumes and their ReadStatuses from the database.
         */
        @Transaction
        fun getAll(): Array<Volume> {
            val volumes = getAllVolumes()
            volumes.forEach {
                it.readStatus = getReadStatus(it.id)
            }
            return volumes
        }

        @Query("SELECT * FROM Volumes WHERE PublisherID = :publisherID")
        fun getByPublisher(publisherID: String): Array<Volume>

        @Delete
        fun delete(vararg volume: Volume)

    }

}