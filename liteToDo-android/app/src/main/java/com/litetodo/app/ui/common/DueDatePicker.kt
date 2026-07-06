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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DueDatePicker(
    dueDate: Long?,
    reminder: Boolean,
    onConfirm: (Long?, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val initialDate = if (dueDate != null) Date(dueDate) else Date()
    val initialMillis = initialDate.time

    var showDatePicker by remember { mutableStateOf(true) }
    var selectedDateMillis by remember { mutableStateOf(initialMillis) }
    var selectedTimeMillis by remember { mutableStateOf(initialMillis) }
    var hasReminder by remember { mutableStateOf(reminder) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis
        )
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedDateMillis = it }
                    showDatePicker = false
                }) { Text("下一步") }
            },
            dismissButton = {
                TextButton(onClick = { onConfirm(null, false); onDismiss() }) { Text("清除") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    } else {
        val timePickerState = rememberTimePickerState(
            initialHour = Date(selectedTimeMillis).hours,
            initialMinute = Date(selectedTimeMillis).minutes,
            is24Hour = true
        )
        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 8.dp) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("选择时间", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(16.dp))
                    TimePicker(state = timePickerState)
                    Spacer(Modifier.height(8.dp))

                    // Reminder toggle
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
                                modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) { Text("取消") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = selectedDateMillis
                            cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            cal.set(Calendar.MINUTE, timePickerState.minute)
                            cal.set(Calendar.SECOND, 0)
                            cal.set(Calendar.MILLISECOND, 0)
                            onConfirm(cal.timeInMillis, hasReminder)
                            onDismiss()
                        }) { Text("确定") }
                    }
                }
            }
        }
    }
}
