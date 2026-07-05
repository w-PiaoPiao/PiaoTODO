package com.litetodo.app.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.litetodo.app.data.dao.CategoryDao
import com.litetodo.app.data.dao.TodoDao
import com.litetodo.app.data.model.CategoryEntity
import com.litetodo.app.data.model.TodoEntity
import com.litetodo.app.domain.model.Category
import com.litetodo.app.domain.model.ProgressNote
import com.litetodo.app.domain.model.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiteToDoRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val todoDao: TodoDao
) {
    private val gson = Gson()
    private val progressNoteListType = object : TypeToken<List<ProgressNote>>() {}.type

    // ---- Entity <-> Domain mapping ----
    private fun TodoEntity.toDomain(): Todo = Todo(
        id = id, content = content, categoryId = categoryId,
        completed = completed, completedAt = completedAt,
        createdAt = createdAt, updatedAt = updatedAt,
        order = `order`, dueDate = dueDate, reminder = reminder,
        progressNotes = gson.fromJson(progressNotesJson, progressNoteListType)
            ?: emptyList(),
        syncId = syncId
    )

    private fun Todo.toEntity(): TodoEntity = TodoEntity(
        id = id, content = content, categoryId = categoryId,
        completed = completed, completedAt = completedAt,
        createdAt = createdAt, updatedAt = updatedAt,
        order = order, dueDate = dueDate, reminder = reminder,
        progressNotesJson = gson.toJson(progressNotes),
        syncId = syncId
    )

    private fun CategoryEntity.toDomain(): Category = Category(
        id = id, name = name, color = color,
        order = `order`, createdAt = createdAt
    )

    private fun Category.toEntity(): CategoryEntity = CategoryEntity(
        id = id, name = name, color = color,
        order = order, createdAt = createdAt
    )

    // ---- Categories ----

    fun categories(): Flow<List<Category>> =
        categoryDao.getAll().map { list -> list.map { it.toDomain() } }

    suspend fun addCategory(name: String, color: String) {
        val now = System.currentTimeMillis()
        val id = "cat_${now}_${UUID.randomUUID().toString().take(6)}"
        val order = categoryDao.count()
        categoryDao.upsert(CategoryEntity(id, name, color, order, now))
    }

    suspend fun updateCategory(id: String, name: String?, color: String?) {
        val existing = categoryDao.getById(id) ?: return
        categoryDao.upsert(
            existing.copy(
                name = name ?: existing.name,
                color = color ?: existing.color
            )
        )
    }

    suspend fun deleteCategory(id: String) {
        val other = categoryDao.getAnyOther(id) ?: return
        todoDao.reassignCategory(id, other.id, System.currentTimeMillis())
        categoryDao.delete(id)
    }

    // ---- Todos ----

    fun incompleteTodos(): Flow<List<Todo>> =
        todoDao.getIncomplete().map { list -> list.map { it.toDomain() } }

    fun incompleteTodosByCategory(categoryId: String): Flow<List<Todo>> =
        todoDao.getIncompleteByCategory(categoryId).map { list -> list.map { it.toDomain() } }

    fun completedTodos(): Flow<List<Todo>> =
        todoDao.getCompleted().map { list -> list.map { it.toDomain() } }

    suspend fun addTodo(todo: Todo) =
        todoDao.upsert(todo.toEntity())

    suspend fun toggleTodo(id: String) {
        val now = System.currentTimeMillis()
        val entity = todoDao.getById(id) ?: return
        if (entity.completed) {
            todoDao.uncomplete(id, now)
        } else {
            todoDao.complete(id, now)
        }
    }

    suspend fun deleteTodo(id: String) =
        todoDao.delete(id)

    suspend fun updateTodoContent(id: String, content: String) =
        todoDao.updateContent(id, content, System.currentTimeMillis())

    suspend fun addProgressNote(todoId: String, noteContent: String) {
        val entity = todoDao.getById(todoId) ?: return
        val notes: MutableList<ProgressNote> = gson.fromJson(
            entity.progressNotesJson, progressNoteListType
        ) ?: mutableListOf()
        notes.add(ProgressNote(
            id = UUID.randomUUID().toString(),
            content = noteContent,
            createdAt = System.currentTimeMillis()
        ))
        todoDao.updateProgressNotes(todoId, gson.toJson(notes), System.currentTimeMillis())
    }

    suspend fun clearCompleted() =
        todoDao.clearCompleted()

    suspend fun initializeDefaults() {
        if (categoryDao.count() > 0) return
        val now = System.currentTimeMillis()
        listOf(
            CategoryEntity("work", "工作", "#3B82F6", 0, now),
            CategoryEntity("life", "生活", "#10B981", 1, now),
            CategoryEntity("entertainment", "娱乐", "#F59E0B", 2, now),
            CategoryEntity("other", "其他", "#6B7280", 3, now),
        ).forEach { categoryDao.upsert(it) }
    }
}
