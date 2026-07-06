package com.litetodo.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.litetodo.app.ui.common.DueDatePicker
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
    var dueDate by remember { mutableStateOf<Long?>(null) }
    var reminder by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    fun formatTs(ts: Long): String {
        val d = java.util.Date(ts)
        val today = java.util.Calendar.getInstance()
        val cal = java.util.Calendar.getInstance().apply { time = d }
        val time = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(d)
        return if (today.get(java.util.Calendar.DAY_OF_YEAR) == cal.get(java.util.Calendar.DAY_OF_YEAR) &&
            today.get(java.util.Calendar.YEAR) == cal.get(java.util.Calendar.YEAR))
            "今天 $time"
        else
            java.text.SimpleDateFormat("M/d HH:mm", java.util.Locale.getDefault()).format(d)
    }

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
            // Input field + clock button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("输入待办事项...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                FilledIconButton(
                    onClick = { showDatePicker = true },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (dueDate != null)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.Default.AccessTime, contentDescription = "设置时间",
                        modifier = Modifier.size(18.dp))
                }
            }

            // Due date badge
            if (dueDate != null) {
                Row(
                    modifier = Modifier.padding(start = 16.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatTs(dueDate!!) + if (reminder) " 🔔" else "",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(Modifier.width(4.dp))
                    IconButton(
                        onClick = { dueDate = null; reminder = false },
                        modifier = Modifier.size(18.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "清除",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Category buttons
            CategoryRow(
                categories = uiState.categories,
                selectedCategoryId = uiState.selectedCategoryId,
                onClickCategory = { categoryId ->
                    if (inputText.isNotBlank()) {
                        viewModel.addTodo(inputText, categoryId, dueDate, reminder)
                        inputText = ""
                        dueDate = null
                        reminder = false
                    } else if (uiState.selectedCategoryId == categoryId) {
                        viewModel.setSelectedCategory(null)
                    } else {
                        viewModel.triggerShake()
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
                            onLongPress = { progressTodoId = todo.id },
                            onPostpone = { ms ->
                                val currentVal = todo.dueDate ?: System.currentTimeMillis()
                                viewModel.updateTodo(todo.id, currentVal + ms)
                            }
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
                onClear = { viewModel.clearCompleted() },
                onToggle = { todoId -> viewModel.toggleTodo(todoId) }
            )
        }
    }

    // Date picker dialog
    if (showDatePicker) {
        DueDatePicker(
            dueDate = dueDate,
            reminder = reminder,
            onConfirm = { d, r -> dueDate = d; reminder = r; showDatePicker = false },
            onDismiss = { showDatePicker = false }
        )
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
