package de.ahahn94.manhattan.model

import androidx.room.Database
import androidx.room.RoomDatabase
import de.ahahn94.manhattan.model.entities.CachedComicEntity
import de.ahahn94.manhattan.model.entities.IssueEntity
import de.ahahn94.manhattan.model.entities.PublisherEntity
import de.ahahn94.manhattan.model.entities.VolumeEntity
import de.ahahn94.manhattan.model.views.PublisherView
import de.ahahn94.manhattan.model.views.VolumeReadStatusView
import de.ahahn94.manhattan.model.views.VolumeView

/**
 * RoomDatabase that wraps the tables and DAOs for the app database.
 */
@Database(
    entities = [PublisherEntity::class, VolumeEntity::class, IssueEntity::class, CachedComicEntity::class],
    views = [VolumeReadStatusView::class, VolumeView::class, PublisherView::class],
    version = 1
)
abstract class ManhattanDatabase : RoomDatabase() {
    abstract fun cachedComicsDao(): CachedComicEntity.CachedComicsDao
    abstract fun issuesDao(): IssueEntity.IssuesDao
    abstract fun publishersDao(): PublisherEntity.PublishersDao
    abstract fun volumesDao(): VolumeEntity.VolumesDao
}