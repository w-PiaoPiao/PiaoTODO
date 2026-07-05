package com.litetodo.app.domain.model

data class Todo(
    val id: String,
    val content: String,
    val categoryId: String,
    val completed: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val order: Int,
    val progressNotes: List<ProgressNote> = emptyList(),
    val syncId: String? = null
)
