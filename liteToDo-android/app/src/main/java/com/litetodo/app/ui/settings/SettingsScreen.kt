package com.litetodo.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.litetodo.app.domain.model.Category
import com.litetodo.app.ui.theme.CategoryColors
import com.litetodo.app.ui.theme.DarkModeOption
import com.litetodo.app.util.parseColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    darkMode: DarkModeOption,
    onDarkModeChange: (DarkModeOption) -> Unit,
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加分类", tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Dark Mode section
            item {
                Text(
                    "外观",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Triple(Icons.Default.WbSunny, "浅色", DarkModeOption.LIGHT),
                        Triple(Icons.Default.DarkMode, "深色", DarkModeOption.DARK),
                        Triple(Icons.Default.PhoneIphone, "跟随系统", DarkModeOption.SYSTEM),
                    ).forEach { (icon, label, option) ->
                        FilterChip(
                            selected = darkMode == option,
                            onClick = { onDarkModeChange(option) },
                            label = { Text(label) },
                            leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    "分类管理",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Category list
            items(
                items = uiState.categories,
                key = { it.id }
            ) { category ->
                CategoryEditItem(
                    category = category,
                    onUpdateName = { name -> viewModel.updateCategoryName(category.id, name) },
                    onUpdateColor = { color -> viewModel.updateCategoryColor(category.id, color) },
                    onDelete = { viewModel.deleteCategory(category.id) },
                    canDelete = uiState.categories.size > 1,
                    categoriesCount = uiState.categories.size
                )
            }

            item {
                Spacer(Modifier.height(72.dp)) // FAB padding
            }
        }
    }

    if (showAddDialog) {
        AddCategoryDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, color ->
                viewModel.addCategory(name, color)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun CategoryEditItem(
    category: Category,
    onUpdateName: (String) -> Unit,
    onUpdateColor: (String) -> Unit,
    onDelete: () -> Unit,
    canDelete: Boolean,
    categoriesCount: Int
) {
    var editing by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf(category.name) }
    var showColorPicker by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color dot
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(parseColor(category.color))
                    .clickable { showColorPicker = true }
            )

            Spacer(Modifier.width(12.dp))

            // Name
            if (editing) {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            } else {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            editing = true
                            editText = category.name
                        }
                )
            }

            if (editing) {
                IconButton(onClick = {
                    if (editText.isNotBlank()) {
                        onUpdateName(editText.trim())
                    }
                    editing = false
                }) {
                    Icon(Icons.Default.Check, contentDescription = "保存", modifier = Modifier.size(18.dp))
                }
            }

            // Delete
            if (canDelete) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "删除",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (showColorPicker) {
        ColorPickerDialog(
            currentColor = category.color,
            onDismiss = { showColorPicker = false },
            onSelect = { color ->
                onUpdateColor(color)
                showColorPicker = false
            }
        )
    }
}

@Composable
private fun ColorPickerDialog(
    currentColor: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择颜色") },
        text = {
            Column {
                val rows = CategoryColors.chunked(4)
                rows.forEach { rowColors ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowColors.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(parseColor(color))
                                    .then(
                                        if (color == currentColor)
                                            Modifier.background(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                                CircleShape
                                            )
                                        else Modifier
                                    )
                                    .clickable { onSelect(color) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (color == currentColor) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

@Composable
private fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(CategoryColors[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加分类") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("分类名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text("选择颜色", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(8.dp))
                val rows = CategoryColors.chunked(4)
                rows.forEach { rowColors ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowColors.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(parseColor(color))
                                    .clickable { selectedColor = color },
                                contentAlignment = Alignment.Center
                            ) {
                                if (color == selectedColor) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name.trim(), selectedColor)
                    }
                }
            ) {
                Text("添加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
