package com.litetodo.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey val id: String,
    val content: String,
    val categoryId: String,
    val completed: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val `order`: Int,
    val dueDate: Long? = null,
    val reminder: Boolean = false,
    val progressNotesJson: String = "[]",
    val syncId: String? = null
)
