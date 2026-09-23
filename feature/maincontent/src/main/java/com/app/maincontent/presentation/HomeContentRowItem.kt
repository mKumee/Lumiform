package com.app.maincontent.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.app.uicomponents.components.CachedNetworkImage
import com.app.uicomponents.components.ChoiceChip
import com.app.uicomponents.components.SectionHeaderCard
import com.app.uicomponents.theme.LumiformColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeContentRowItem(
    row: ContentRow,
    selections: Map<Int, Set<Int>>,
    isSectionExpanded: Boolean,
    onSectionToggled: (Int) -> Unit,
    onResponseToggled: (questionId: Int, responseId: Int, allowMultiple: Boolean) -> Unit,
    onImageClick: (src: String, title: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LumiformColors.current
    val indent = ((row.depth - 1).coerceAtLeast(0) * 16).dp
    val isTopLevel = (row.depth - 1) <= 0
    val cardShape = RoundedCornerShape(if (isTopLevel) 20.dp else 16.dp)
    val cardShadowElevation = if (isTopLevel) 14.dp else 8.dp
    val cardShadowAlpha = if (isTopLevel) 0.16f else 0.12f

    when (row.type) {
        ContentRowType.PAGE -> Unit

        ContentRowType.SECTION -> SectionHeaderCard(
            title = row.title.orEmpty(),
            depth = (row.depth - 1).coerceAtLeast(0),
            expanded = isSectionExpanded,
            onToggle = { onSectionToggled(row.id) },
            modifier = modifier.fillMaxWidth().padding(start = indent)
        )

        ContentRowType.TEXT -> Column(
            modifier = modifier.fillMaxWidth().padding(start = indent + 2.dp, top = 2.dp, bottom = 2.dp)
        ) {
            Text(text = row.content.orEmpty(), style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
        }

        ContentRowType.IMAGE -> Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = indent)
                .shadow(cardShadowElevation, cardShape, ambientColor = colors.shadowAmbient.copy(alpha = 0.04f), spotColor = colors.shadowSpot.copy(alpha = cardShadowAlpha))
                .clip(cardShape)
                .background(colors.cardSurface)
                .clickable { onImageClick(row.imageSrc.orEmpty(), row.title.orEmpty()) }
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CachedNetworkImage(url = row.imageSrc.orEmpty(), contentDescription = row.title, thumbnailSize = true)
            Text(text = row.title.orEmpty(), style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
        }

        ContentRowType.CHOICE -> Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = indent)
                .shadow(cardShadowElevation, cardShape, ambientColor = colors.shadowAmbient.copy(alpha = 0.04f), spotColor = colors.shadowSpot.copy(alpha = cardShadowAlpha))
                .clip(cardShape)
                .background(colors.cardSurface)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = row.content.orEmpty(), style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
            val responseSet = row.responseSet
            if (responseSet != null) {
                val selectedIds = selections[row.id].orEmpty()
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    responseSet.responses.forEach { response ->
                        ChoiceChip(
                            label = response.label,
                            selected = response.id in selectedIds,
                            onClick = { onResponseToggled(row.id, response.id, responseSet.multipleSelection) }
                        )
                    }
                }
                Text(
                    text = if (responseSet.multipleSelection) "Multiple choice" else "Single choice",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted
                )
            }
        }
    }
}
