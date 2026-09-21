package com.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.core.database.dao.ContentDao
import com.app.core.database.entity.ItemEntity
import com.app.core.database.entity.PageEntity
import com.app.core.database.entity.ResponseEntity
import com.app.core.database.entity.ResponseSetEntity


@Database(
    entities = [PageEntity::class, ItemEntity::class, ResponseSetEntity::class, ResponseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LumiformDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao

    companion object {
        const val DATABASE_NAME = "lumiform.db"
    }
}
