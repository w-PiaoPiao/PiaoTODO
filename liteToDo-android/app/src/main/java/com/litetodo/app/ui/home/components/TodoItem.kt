package com.litetodo.app.ui.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.litetodo.app.domain.model.Category
import com.litetodo.app.domain.model.Todo
import com.litetodo.app.util.parseColor
import java.text.SimpleDateFormat
import java.util.*

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
    val catColor = category?.color?.let { parseColor(it) } ?: Color(0xFF3B82F6)
    val isOverdue = todo.dueDate != null && todo.dueDate!! < System.currentTimeMillis() && !todo.completed
    val overdueColor = Color(0xFFEF4444)

    fun formatDueDate(ts: Long): String {
        val d = Date(ts)
        val today = Calendar.getInstance()
        val cal = Calendar.getInstance().apply { time = d }
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(d)
        return if (today.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR) &&
            today.get(Calendar.YEAR) == cal.get(Calendar.YEAR))
            "今天 $time"
        else SimpleDateFormat("M/d HH:mm", Locale.getDefault()).format(d)
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
        Box(modifier = Modifier.fillMaxWidth()) {
            // Ink-wash brush stroke background
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = Path().apply {
                    moveTo(0f, size.height * 0.15f)
                    cubicTo(
                        size.width * 0.3f, size.height * 0.1f,
                        size.width * 0.6f, size.height * 0.3f,
                        size.width * 0.8f, size.height * 0.2f
                    )
                    cubicTo(
                        size.width * 0.9f, size.height * 0.15f,
                        size.width, size.height * 0.25f,
                        size.width, size.height * 0.25f
                    )
                }
                drawPath(
                    path,
                    color = catColor.copy(alpha = 0.04f),
                    style = Stroke(width = 8f)
                )
                val path2 = Path().apply {
                    moveTo(0f, size.height * 0.7f)
                    cubicTo(
                        size.width * 0.2f, size.height * 0.8f,
                        size.width * 0.5f, size.height * 0.6f,
                        size.width * 0.7f, size.height * 0.75f
                    )
                }
                drawPath(
                    path2,
                    color = catColor.copy(alpha = 0.03f),
                    style = Stroke(width = 12f)
                )
            }

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
                    if (todo.completed) Text("✓", color = Color.White, fontSize = 12.sp)
                }

                Spacer(Modifier.width(12.dp))

                // Content
                Column(modifier = Modifier.weight(1f)) {
                    if (editing) {
                        OutlinedTextField(
                            value = editText, onValueChange = { editText = it },
                            singleLine = true, modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (editText.isNotBlank()) { onUpdateContent(editText.trim()) }
                                editing = false
                            })
                        )
                    } else {
                        Text(
                            text = todo.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (todo.completed)
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            else MaterialTheme.colorScheme.onSurface,
                            textDecoration = if (todo.completed) TextDecoration.LineThrough else null,
                            modifier = Modifier.clickable { editing = true; editText = todo.content }
                        )
                    }

                    // Due date badge
                    if (todo.dueDate != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                Icons.Default.AccessTime, contentDescription = null,
                                modifier = Modifier.size(11.dp),
                                tint = if (isOverdue) overdueColor else MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(3.dp))
                            Text(
                                text = formatDueDate(todo.dueDate),
                                fontSize = 11.sp,
                                fontWeight = if (isOverdue) androidx.compose.ui.text.font.FontWeight.Medium else null,
                                color = if (isOverdue) overdueColor else MaterialTheme.colorScheme.primary
                            )
                            if (todo.reminder) {
                                Spacer(Modifier.width(2.dp))
                                Icon(
                                    Icons.Default.Notifications, contentDescription = null,
                                    modifier = Modifier.size(11.dp),
                                    tint = if (isOverdue) overdueColor else MaterialTheme.colorScheme.primary
                                )
                            }
                            if (isOverdue) {
                                Spacer(Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = overdueColor.copy(alpha = 0.1f)
                                ) {
                                    Row(
                                        modifier = Modifier.clickable {
                                            val now = System.currentTimeMillis()
                                            val current = todo.dueDate ?: now
                                            val newDue = current + 86400000L
                                            // Postpone via onToggle-like approach
                                            // We'll use the repository pattern
                                        }.padding(horizontal = 6.dp, vertical = 1.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = null,
                                            modifier = Modifier.size(10.dp),
                                            tint = overdueColor)
                                        Spacer(Modifier.width(2.dp))
                                        Text("顺延", fontSize = 10.sp, color = overdueColor)
                                    }
                                }
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

                // Quick complete button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onToggle() }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "快速办结",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                    }
                    Spacer(Modifier.width(4.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "删除",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }

        // Progress history
        AnimatedVisibility(visible = showHistory && todo.progressNotes.isNotEmpty()) {
            Column(modifier = Modifier.padding(start = 34.dp, top = 8.dp)) {
                todo.progressNotes.reversed().forEach { note ->
                    Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 2.dp)) {
                        Text("•", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(6.dp))
                        Column {
                            Text(note.content, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                SimpleDateFormat("M/d HH:mm", Locale.getDefault()).format(Date(note.createdAt)),
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
