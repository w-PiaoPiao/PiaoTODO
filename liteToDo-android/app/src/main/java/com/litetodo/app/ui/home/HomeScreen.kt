package com.litetodo.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.litetodo.app.ui.common.ProgressDialog
import com.litetodo.app.ui.home.components.CategoryRow
import com.litetodo.app.ui.home.components.CompletedSection
import com.litetodo.app.ui.home.components.TodoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var inputText by remember { mutableStateOf("") }
    var progressTodoId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.setSelectedCategory(null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.DoneAll,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "LiteToDo",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "设置",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Input field
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("输入待办事项，点击下方分类按钮添加...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            // Category buttons
            CategoryRow(
                categories = uiState.categories,
                selectedCategoryId = uiState.selectedCategoryId,
                onClickCategory = { categoryId ->
                    if (inputText.isNotBlank()) {
                        viewModel.addTodo(inputText, categoryId)
                        inputText = ""
                    } else {
                        // Click again when filter active removes filter
                        if (uiState.selectedCategoryId == categoryId) {
                            viewModel.setSelectedCategory(null)
                        }
                    }
                },
                onLongPressCategory = { categoryId ->
                    viewModel.setSelectedCategory(categoryId)
                },
                shaking = uiState.shakeCategories
            )

            // Todo list
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.incompleteTodos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "还没有 todo",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "在上方输入内容，然后选择一个分类添加",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(
                        items = uiState.incompleteTodos,
                        key = { it.id }
                    ) { todo ->
                        val category = uiState.categories.find { it.id == todo.categoryId }
                        TodoItem(
                            todo = todo,
                            category = category,
                            onToggle = { viewModel.toggleTodo(todo.id) },
                            onDelete = { viewModel.deleteTodo(todo.id) },
                            onUpdateContent = { content ->
                                viewModel.updateTodoContent(todo.id, content)
                            },
                            onLongPress = { progressTodoId = todo.id }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    }
                }
            }

            // Completed section
            CompletedSection(
                completedTodos = uiState.completedTodos,
                categories = uiState.categories,
                onClear = { viewModel.clearCompleted() }
            )
        }
    }

    // Progress dialog
    if (progressTodoId != null) {
        val todo = uiState.incompleteTodos.find { it.id == progressTodoId }
            ?: uiState.completedTodos.find { it.id == progressTodoId }
        if (todo != null) {
            ProgressDialog(
                todoContent = todo.content,
                onDismiss = { progressTodoId = null },
                onSubmit = { content ->
                    viewModel.addProgressNote(todo.id, content)
                }
            )
        } else {
            LaunchedEffect(Unit) { progressTodoId = null }
        }
    }
}
