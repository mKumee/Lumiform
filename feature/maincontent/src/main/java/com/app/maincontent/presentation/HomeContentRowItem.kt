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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.app.core.model.Response
import com.app.core.model.ResponseSet
import com.app.core.model.ThemeMode
import com.app.uicomponents.components.CachedNetworkImage
import com.app.uicomponents.components.ChoiceChip
import com.app.uicomponents.components.SectionHeaderCard
import com.app.uicomponents.theme.LumiformColors
import com.app.uicomponents.theme.LumiformTheme

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
            Text(text = row.title.orEmpty(), style = MaterialTheme.typography.bodySmall, color = colors.textPrimary)
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
            Text(text = row.content.orEmpty(), style = MaterialTheme.typography.bodySmall, color = colors.textPrimary)
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
//preview mode
@Preview(name = "Section - top level", showBackground = true)
@Composable
private fun SectionRowTopLevelPreview() {
    PreviewSurface {
        HomeContentRowItem(
            row = ContentRow(id = 1, depth = 1, type = ContentRowType.SECTION, ancestorSectionIds = emptyList(), title = "Introduction", orderLabel = "01"),
            selections = emptyMap(),
            isSectionExpanded = true,
            onSectionToggled = {},
            onResponseToggled = { _, _, _ -> },
            onImageClick = { _, _ -> }
        )
    }
}

@Preview(name = "Section - nested", showBackground = true)
@Composable
private fun SectionRowNestedPreview() {
    PreviewSurface {
        HomeContentRowItem(
            row = ContentRow(id = 7, depth = 2, type = ContentRowType.SECTION, ancestorSectionIds = listOf(5), title = "Subsection 1.1"),
            selections = emptyMap(),
            isSectionExpanded = true,
            onSectionToggled = {},
            onResponseToggled = { _, _, _ -> },
            onImageClick = { _, _ -> }
        )
    }
}

@Preview(name = "Text question", showBackground = true)
@Composable
private fun TextRowPreview() {
    PreviewSurface {
        HomeContentRowItem(
            row = ContentRow(id = 8, depth = 3, type = ContentRowType.TEXT, ancestorSectionIds = listOf(5, 7), content = "This is a subsection under Chapter 1."),
            selections = emptyMap(),
            isSectionExpanded = true,
            onSectionToggled = {},
            onResponseToggled = { _, _, _ -> },
            onImageClick = { _, _ -> }
        )
    }
}

@Preview(name = "Image question", showBackground = true)
@Composable
private fun ImageRowPreview() {
    PreviewSurface {
        HomeContentRowItem(
            row = ContentRow(id = 9, depth = 2, type = ContentRowType.IMAGE, ancestorSectionIds = listOf(5), title = "Chapter 1 Image", imageSrc = "https://example.com/chapter1.png"),
            selections = emptyMap(),
            isSectionExpanded = true,
            onSectionToggled = {},
            onResponseToggled = { _, _, _ -> },
            onImageClick = { _, _ -> }
        )
    }
}

@PreviewLightDark
@Composable
private fun ChoiceRowSingleSelectPreview() {
    PreviewSurface {
        HomeContentRowItem(
            row = ContentRow(
                id = 13, depth = 2, type = ContentRowType.CHOICE, ancestorSectionIds = listOf(11),
                content = "What is the main topic of Chapter 2?",
                responseSet = ResponseSet(
                    id = 101, multipleSelection = false,
                    responses = listOf(
                        Response(id = 1011, label = "Label 1", score = 1),
                        Response(id = 1012, label = "Label2", score = 2),
                        Response(id = 1013, label = "Label 3", score = null)
                    )
                )
            ),
            selections = mapOf(13 to setOf(1011)),
            isSectionExpanded = true,
            onSectionToggled = {},
            onResponseToggled = { _, _, _ -> },
            onImageClick = { _, _ -> }
        )
    }
}

@Preview(name = "Choice question - multi select", showBackground = true)
@Composable
private fun ChoiceRowMultiSelectPreview() {
    PreviewSurface {
        HomeContentRowItem(
            row = ContentRow(
                id = 14, depth = 2, type = ContentRowType.CHOICE, ancestorSectionIds = listOf(11),
                content = "Which areas were inspected?",
                responseSet = ResponseSet(
                    id = 102, multipleSelection = true,
                    responses = listOf(
                        Response(id = 1021, label = "Label 1", score = null),
                        Response(id = 1022, label = "Label 2", score = null),
                        Response(id = 1023, label = "Label 3", score = null)
                    )
                )
            ),
            selections = mapOf(14 to setOf(1021, 1023)),
            isSectionExpanded = true,
            onSectionToggled = {},
            onResponseToggled = { _, _, _ -> },
            onImageClick = { _, _ -> }
        )
    }
}

@Composable
private fun PreviewSurface(content: @Composable () -> Unit) {
    LumiformTheme(ThemeMode.LIGHT) {
        Surface(color = LumiformColors.current.background) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                content()
            }
        }
    }
}
