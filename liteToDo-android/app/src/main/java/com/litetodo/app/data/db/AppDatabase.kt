package com.litetodo.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.litetodo.app.data.dao.CategoryDao
import com.litetodo.app.data.dao.TodoDao
import com.litetodo.app.data.model.CategoryEntity
import com.litetodo.app.data.model.TodoEntity

@Database(
    entities = [CategoryEntity::class, TodoEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun todoDao(): TodoDao
}

val MIGRATION_1_2 = Migration(1, 2) { db ->
    db.execSQL("ALTER TABLE todos ADD COLUMN dueDate INTEGER DEFAULT NULL")
    db.execSQL("ALTER TABLE todos ADD COLUMN reminder INTEGER NOT NULL DEFAULT 0")
}
