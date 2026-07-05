package com.litetodo.app.domain.model

data class Category(
    val id: String,
    val name: String,
    val color: String,
    val order: Int,
    val createdAt: Long
)
