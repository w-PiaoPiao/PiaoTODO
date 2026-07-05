package com.litetodo.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litetodo.app.data.repository.LiteToDoRepository
import com.litetodo.app.domain.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: LiteToDoRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = repository.categories()
        .map { categories -> SettingsUiState(categories = categories, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun addCategory(name: String, color: String) {
        viewModelScope.launch { repository.addCategory(name, color) }
    }

    fun updateCategoryName(id: String, name: String) {
        viewModelScope.launch { repository.updateCategory(id, name, null) }
    }

    fun updateCategoryColor(id: String, color: String) {
        viewModelScope.launch { repository.updateCategory(id, null, color) }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch { repository.deleteCategory(id) }
    }
}
