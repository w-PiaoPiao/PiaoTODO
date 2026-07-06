package com.litetodo.app.ui.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.DpOffset
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
    onPostpone: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val isOverdue = todo.dueDate != null && todo.dueDate!! < System.currentTimeMillis() && !todo.completed
    val catColor = category?.color?.let { parseColor(it) } ?: Color(0xFF3B82F6)
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
            .drawBehind {
                drawRect(
                    color = catColor.copy(alpha = 0.3f),
                    topLeft = Offset(0f, 0f),
                    size = androidx.compose.ui.geometry.Size(3.dp.toPx(), size.height)
                )
            }
            .clickable { showHistory = !showHistory }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Complete circle
            Box(
                modifier = Modifier.size(22.dp).clip(CircleShape)
                    .background(
                        if (todo.completed) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ).clickable { onToggle() },
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
                            color = MaterialTheme.colorScheme.onSurface),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent),
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
                        color = if (todo.completed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (todo.completed) TextDecoration.LineThrough else null,
                        modifier = Modifier.clickable { editing = true; editText = todo.content }
                    )
                }

                // Due date badge
                if (todo.dueDate != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                        Icon(Icons.Default.AccessTime, contentDescription = null,
                            modifier = Modifier.size(11.dp),
                            tint = if (isOverdue) overdueColor else MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = formatDueDate(todo.dueDate), fontSize = 11.sp,
                            fontWeight = if (isOverdue) FontWeight.Medium else null,
                            color = if (isOverdue) overdueColor else MaterialTheme.colorScheme.primary)
                        if (todo.reminder) {
                            Spacer(Modifier.width(2.dp))
                            Icon(Icons.Default.Notifications, contentDescription = null,
                                modifier = Modifier.size(11.dp),
                                tint = if (isOverdue) overdueColor else MaterialTheme.colorScheme.primary)
                        }
                        if (isOverdue) {
                            PostponeButton(onPostpone = { ms -> onPostpone(ms) })
                        }
                    }
                }

                if (todo.progressNotes.isNotEmpty()) {
                    Text(text = "└ ${todo.progressNotes.last().content}",
                        fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp))
                }
            }

            Spacer(Modifier.width(8.dp))
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PostponeButton(onPostpone: (Long) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = Color(0xFFEF4444).copy(alpha = 0.1f),
            modifier = Modifier.combinedClickable(
                onClick = { onPostpone(86400000L) },
                onLongClick = { expanded = true }
            )
        ) {
            Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Refresh, contentDescription = null,
                    modifier = Modifier.size(10.dp), tint = Color(0xFFEF4444))
                Spacer(Modifier.width(2.dp))
                Text("顺延", fontSize = 10.sp, color = Color(0xFFEF4444))
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset.Zero
        ) {
            listOf(
                30 * 60 * 1000L to "30 分钟",
                60 * 60 * 1000L to "1 小时",
                3 * 60 * 60 * 1000L to "3 小时",
                24 * 60 * 60 * 1000L to "1 天",
                3 * 24 * 60 * 60 * 1000L to "3 天",
            ).forEach { (ms, label) ->
                DropdownMenuItem(
                    text = { Text(label, fontSize = 13.sp) },
                    onClick = { onPostpone(ms); expanded = false }
                )
            }
        }
    }
}
