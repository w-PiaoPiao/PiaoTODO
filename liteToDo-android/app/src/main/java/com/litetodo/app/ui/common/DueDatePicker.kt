package com.litetodo.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DueDatePicker(
    dueDate: Long?,
    reminder: Boolean,
    onConfirm: (Long?, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val now = dueDate?.let { Date(it) } ?: Date()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val stf = SimpleDateFormat("HH:mm", Locale.getDefault())

    var dateStr by remember { mutableStateOf(sdf.format(now)) }
    var timeStr by remember { mutableStateOf(stf.format(now)) }
    var hasReminder by remember { mutableStateOf(reminder) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 8.dp) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("设置到期时间", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(16.dp))

                Text("日期", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = dateStr,
                    onValueChange = { dateStr = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(Modifier.height(12.dp))
                Text("时间", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = timeStr,
                    onValueChange = { timeStr = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (hasReminder) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { hasReminder = !hasReminder }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (hasReminder) "闹钟提醒已开启" else "闹钟提醒已关闭",
                        fontSize = 13.sp,
                        color = if (hasReminder) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.weight(1f))
                    if (hasReminder) {
                        Icon(Icons.Default.Check, contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {
                        onConfirm(null, false)
                        onDismiss()
                    }) { Text("清除时间", color = MaterialTheme.colorScheme.error) }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = onDismiss) { Text("取消") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        try {
                            val ts = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                                .parse("$dateStr $timeStr")?.time
                            onConfirm(ts, hasReminder)
                        } catch (_: Exception) { onConfirm(null, false) }
                        onDismiss()
                    }) { Text("确定") }
                }
            }
        }
    }
}
