package com.app.uicomponents.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.app.uicomponents.theme.LumiformColors

@Composable
fun ChoiceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LumiformColors.current
    val shape = RoundedCornerShape(999.dp)

    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = if (selected) colors.onAccent else colors.accent,
        modifier = modifier
            .clip(shape)
            .background(if (selected) colors.accent else colors.accentTint)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp)
    )
}
