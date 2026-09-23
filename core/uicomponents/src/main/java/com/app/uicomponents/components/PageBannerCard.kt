package com.app.uicomponents.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.app.core.model.ThemeMode
import com.app.uicomponents.theme.LumiformColors



@Composable
fun PageBannerCard(
    title: String,
    pageNumber: Int,
    totalPages: Int,
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LumiformColors.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
            Brush.linearGradient(
                    colors = listOf(colors.accent.copy(alpha = 0.10f), colors.background),
                    start = Offset(x = 900f, y = 0f),
                    end = Offset(x = 250f, y = 260f)
                )
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                if (totalPages > 1) {
                    Text(
                        text = "PAGE $pageNumber OF $totalPages",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.accent
                    )
                }
                Text(text = title, style = MaterialTheme.typography.headlineMedium, color = colors.textPrimary)
            }

            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier
                    .size(40.dp)
                    .shadow(elevation = 10.dp, shape = CircleShape, ambientColor = colors.shadowSpot.copy(alpha = 0.3f), spotColor = colors.shadowSpot.copy(alpha = 0.3f))
                    .clip(CircleShape)
                    .background(colors.cardSurface)
            ) {
                Icon(
                    imageVector = if (themeMode == ThemeMode.LIGHT) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                    contentDescription = if (themeMode == ThemeMode.LIGHT) "Switch to dark theme" else "Switch to light theme",
                    tint = colors.accent
                )
            }
        }
    }
}
