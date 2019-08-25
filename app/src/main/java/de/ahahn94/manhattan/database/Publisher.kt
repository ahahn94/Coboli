package de.ahahn94.manhattan.database

import androidx.annotation.NonNull
import androidx.room.*
import com.google.gson.annotations.SerializedName

/**
 * Data class for publisher datasets.
 */
@Entity(tableName = "Publishers")
data class Publisher(
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

    @SerializedName("VolumesURL")
    @ColumnInfo(name = "VolumesURL")
    var volumesURL: String = "",

    @SerializedName("VolumesCount")
    @Ignore
    var volumesCount: Int = 0
) {

    /**
     * Data access object for the Publishers database table.
     */
    @Dao
    interface PublishersDao{
        @Insert(onConflict = OnConflictStrategy.ABORT)
        fun insert(publisher: Publisher)

        @Query("SELECT * FROM Publishers")
        fun getAll() : Array<Publisher>
    }

}