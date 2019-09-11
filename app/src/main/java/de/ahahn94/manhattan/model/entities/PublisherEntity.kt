package de.ahahn94.manhattan.model.entities

import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.google.gson.annotations.SerializedName

/**
 * Entity data class for publisher datasets.
 */
@Entity(tableName = "Publishers")
data class PublisherEntity(
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
    @ColumnInfo(name = "VolumesCount")
    var volumesCount: Int = 0
) {

    /**
     * Data access object for the publishers database table.
     */
    @Dao
    interface PublishersDao {
        @Insert(onConflict = OnConflictStrategy.ABORT)
        fun insert(publisherEntity: PublisherEntity)

        @Update
        fun update(vararg publisherEntity: PublisherEntity)

        @Query("SELECT * FROM PublisherView WHERE ID = :publisherID")
        fun get(publisherID: String): PublisherEntity?

        @Query("SELECT * FROM PublisherView")
        fun getAll(): Array<PublisherEntity>

        @Delete
        fun delete(vararg publisherEntity: PublisherEntity)

        @Query("SELECT * FROM PublisherView")
        fun getAllPaged(): DataSource.Factory<Int, PublisherEntity>

        @Query("SELECT * FROM PublisherView WHERE ID = :publisherID")
        fun getLiveData(publisherID: String): LiveData<PublisherEntity>

    }

}