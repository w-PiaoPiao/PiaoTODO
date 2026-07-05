package com.litetodo.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.litetodo.app.data.dao.CategoryDao
import com.litetodo.app.data.dao.TodoDao
import com.litetodo.app.data.model.CategoryEntity
import com.litetodo.app.data.model.TodoEntity

@Database(
    entities = [CategoryEntity::class, TodoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun todoDao(): TodoDao
}
