package com.litetodo.app.ui.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.litetodo.app.domain.model.Category
import com.litetodo.app.domain.model.Todo
import com.litetodo.app.util.parseColor

@Composable
fun TodoItem(
    todo: Todo,
    category: Category?,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onUpdateContent: (String) -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    fun formatDueDate(ts: Long): String {
        val d = Date(ts)
        val today = Calendar.getInstance()
        val cal = Calendar.getInstance().apply { time = d }
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(d)
        return if (today.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR) &&
            today.get(Calendar.YEAR) == cal.get(Calendar.YEAR))
            "今天 $time"
        else
            SimpleDateFormat("M/d HH:mm", Locale.getDefault()).format(d)
    }

    var editing by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf(todo.content) }
    var showHistory by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { showHistory = !showHistory }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Complete circle
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(
                        if (todo.completed) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (todo.completed) {
                    Text("✓", color = Color.White, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.width(12.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                if (editing) {
                    OutlinedTextField(
                        value = editText,
                        onValueChange = { editText = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (editText.isNotBlank()) {
                                    onUpdateContent(editText.trim())
                                }
                                editing = false
                            }
                        )
                    )
                } else {
                    Text(
                        text = todo.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (todo.completed)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (todo.completed) TextDecoration.LineThrough else null,
                        modifier = Modifier.clickable {
                            editing = true
                            editText = todo.content
                        }
                    )
                }

                // Due date badge
                if (todo.dueDate != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(11.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = formatDueDate(todo.dueDate),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (todo.reminder) {
                            Spacer(Modifier.width(2.dp))
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = null,
                                modifier = Modifier.size(11.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                if (todo.progressNotes.isNotEmpty()) {
                    Text(
                        text = "└ ${todo.progressNotes.last().content}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            // Category dot + delete
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (category != null) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(parseColor(category.color))
                    )
                }
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "删除",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // Progress history
        AnimatedVisibility(visible = showHistory && todo.progressNotes.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .padding(start = 34.dp, top = 8.dp)
                    .padding(start = 12.dp)
            ) {
                todo.progressNotes.reversed().forEach { note ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text("•", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(6.dp))
                        Column {
                            Text(note.content, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                java.text.SimpleDateFormat("M/d HH:mm", java.util.Locale.getDefault())
                                    .format(java.util.Date(note.createdAt)),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}
