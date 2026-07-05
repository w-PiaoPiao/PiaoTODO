package com.litetodo.app.ui.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.litetodo.app.domain.model.Category
import com.litetodo.app.util.parseColor

@Composable
fun RowScope.CategoryButton(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val bgColor = parseColor(category.color)

    Box(
        modifier = Modifier
            .weight(1f)
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) bgColor.copy(alpha = 0.9f) else bgColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onLongClick() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = category.name,
            color = Color.White,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CategoryRow(
    categories: List<Category>,
    selectedCategoryId: String?,
    onClickCategory: (String) -> Unit,
    onLongPressCategory: (String) -> Unit,
    shaking: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { category ->
                CategoryButton(
                    category = category,
                    isSelected = category.id == selectedCategoryId,
                    onClick = { onClickCategory(category.id) },
                    onLongClick = { onLongPressCategory(category.id) }
                )
            }
        }

        if (selectedCategoryId != null) {
            val cat = categories.find { it.id == selectedCategoryId }
            if (cat != null) {
                FilterBanner(
                    categoryName = cat.name,
                    onCancel = { onClickCategory(selectedCategoryId) }
                )
            }
        }
    }
}

@Composable
private fun FilterBanner(
    categoryName: String,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "只看：$categoryName",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.weight(1f))
        TextButton(onClick = onCancel) {
            Text("取消筛选", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}
