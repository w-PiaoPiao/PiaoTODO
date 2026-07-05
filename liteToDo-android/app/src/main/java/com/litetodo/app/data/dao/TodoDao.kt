package com.litetodo.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.litetodo.app.data.model.TodoEntity

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos ORDER BY `order` ASC")
    fun getAll(): kotlinx.coroutines.flow.Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE completed = 0 ORDER BY `order` ASC")
    fun getIncomplete(): kotlinx.coroutines.flow.Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE completed = 1 ORDER BY completedAt DESC")
    fun getCompleted(): kotlinx.coroutines.flow.Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE categoryId = :categoryId AND completed = 0 ORDER BY `order` ASC")
    fun getIncompleteByCategory(categoryId: String): kotlinx.coroutines.flow.Flow<List<TodoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(todo: TodoEntity)

    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getById(id: String): TodoEntity?

    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM todos WHERE completed = 1")
    suspend fun clearCompleted()

    @Query("UPDATE todos SET content = :content, updatedAt = :now WHERE id = :id")
    suspend fun updateContent(id: String, content: String, now: Long)

    @Query("UPDATE todos SET completed = 1, completedAt = :now, updatedAt = :now WHERE id = :id")
    suspend fun complete(id: String, now: Long)

    @Query("UPDATE todos SET completed = 0, completedAt = NULL, updatedAt = :now WHERE id = :id")
    suspend fun uncomplete(id: String, now: Long)

    @Query("UPDATE todos SET progressNotesJson = :json, updatedAt = :now WHERE id = :id")
    suspend fun updateProgressNotes(id: String, json: String, now: Long)

    @Query("UPDATE todos SET categoryId = :newCategoryId, updatedAt = :now WHERE categoryId = :oldCategoryId")
    suspend fun reassignCategory(oldCategoryId: String, newCategoryId: String, now: Long)
}
