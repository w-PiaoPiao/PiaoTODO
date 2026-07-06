package com.litetodo.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litetodo.app.data.repository.LiteToDoRepository
import com.litetodo.app.domain.model.Category
import com.litetodo.app.domain.model.ProgressNote
import com.litetodo.app.domain.model.Todo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class HomeUiState(
    val categories: List<Category> = emptyList(),
    val incompleteTodos: List<Todo> = emptyList(),
    val completedTodos: List<Todo> = emptyList(),
    val selectedCategoryId: String? = null,
    val shakeCategories: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: LiteToDoRepository
) : ViewModel() {

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    private val _shakeCategories = MutableStateFlow(false)

    private val incomplete: StateFlow<List<Todo>> = _selectedCategoryId.flatMapLatest { catId ->
        if (catId == null) repository.incompleteTodos()
        else repository.incompleteTodosByCategory(catId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<HomeUiState> = combine(
        repository.categories(),
        incomplete,
        repository.completedTodos(),
        _selectedCategoryId,
        _shakeCategories
    ) { cats, inc, comp, selId, shake ->
        HomeUiState(
            categories = cats,
            incompleteTodos = inc,
            completedTodos = comp,
            selectedCategoryId = selId,
            shakeCategories = shake,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    init {
        viewModelScope.launch {
            repository.initializeDefaults()
        }
    }

    fun triggerShake() {
        _shakeCategories.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            _shakeCategories.value = false
        }
    }

    fun addTodo(content: String, categoryId: String, dueDate: Long? = null, reminder: Boolean = false) {
        val trimmed = content.trim()
        if (trimmed.isEmpty()) {
            triggerShake()
            return
        }
        val now = System.currentTimeMillis()
        viewModelScope.launch {
            repository.addTodo(
                Todo(
                    id = UUID.randomUUID().toString(),
                    content = trimmed,
                    categoryId = categoryId,
                    completed = false,
                    completedAt = null,
                    createdAt = now,
                    updatedAt = now,
                    order = 0,
                    dueDate = dueDate,
                    reminder = reminder,
                    progressNotes = emptyList(),
                    syncId = null
                )
            )
        }
    }

    fun toggleTodo(todoId: String) {
        viewModelScope.launch { repository.toggleTodo(todoId) }
    }

    fun deleteTodo(todoId: String) {
        viewModelScope.launch { repository.deleteTodo(todoId) }
    }

    fun updateTodoContent(todoId: String, content: String) {
        viewModelScope.launch { repository.updateTodoContent(todoId, content.trim()) }
    }

    fun addProgressNote(todoId: String, content: String) {
        viewModelScope.launch { repository.addProgressNote(todoId, content.trim()) }
    }

    fun clearCompleted() {
        viewModelScope.launch { repository.clearCompleted() }
    }

    fun setSelectedCategory(categoryId: String?) {
        _selectedCategoryId.value = categoryId
    }

    fun updateTodo(todoId: String, dueDate: Long) {
        viewModelScope.launch { repository.updateDueDate(todoId, dueDate) }
    }
}
