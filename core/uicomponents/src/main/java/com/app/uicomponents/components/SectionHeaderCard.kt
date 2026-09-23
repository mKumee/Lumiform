package com.app.uicomponents.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.app.uicomponents.theme.LumiformColors
import com.app.uicomponents.theme.FontSizes

@Composable
fun SectionHeaderCard(
    title: String,
    depth: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LumiformColors.current
    val rotation = animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "chevron_rotation").value
    val isTopLevel = depth == 0
    val shape = RoundedCornerShape(if (isTopLevel) 20.dp else 16.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isTopLevel) 14.dp else 8.dp,
                shape = shape,
                ambientColor = colors.shadowAmbient.copy(alpha = 0.05f),
                spotColor = colors.shadowSpot.copy(alpha = if (isTopLevel) 0.18f else 0.12f)
            )
            .clip(shape)
            .background(colors.cardSurface)
            .clickable(onClick = onToggle)
            .padding(horizontal = 18.dp, vertical = if (isTopLevel) 17.dp else 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = FontSizes.sectionFontSizeStyle(depth, MaterialTheme.typography),
            color = colors.textPrimary
        )
        Icon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = if (expanded) "Collapse section" else "Expand section",
            tint = colors.accent,
            modifier = Modifier.rotate(rotation)
        )
    }
}
