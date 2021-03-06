package de.ahahn94.coboli.model.entities

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

    @SerializedName("Description")
    @ColumnInfo(name = "Description")
    var description: String = "",

    @SerializedName("ImageFileURL")
    @ColumnInfo(name = "ImageFileURL")
    var imageFileURL: String = "",

    @SerializedName("Name")
    @ColumnInfo(name = "Name")
    var name: String = "",

    @SerializedName("VolumeCount")
    @ColumnInfo(name = "VolumeCount")
    var volumeCount: Int = 0
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

        @Query("SELECT * FROM PublishersView WHERE ID = :publisherID")
        fun get(publisherID: String): PublisherEntity?

        @Query("SELECT * FROM PublishersView")
        fun getAll(): Array<PublisherEntity>

        @Delete
        fun delete(vararg publisherEntity: PublisherEntity)

        @Query("SELECT * FROM PublishersView ORDER BY Name")
        fun getAllPaged(): DataSource.Factory<Int, PublisherEntity>

        @Query("SELECT * FROM PublishersView WHERE ID = :publisherID")
        fun getLiveData(publisherID: String): LiveData<PublisherEntity>

    }

}