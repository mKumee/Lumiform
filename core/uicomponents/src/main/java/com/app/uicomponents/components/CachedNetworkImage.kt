package com.app.uicomponents.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.app.uicomponents.theme.LumiformColors


@Composable
fun CachedNetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    thumbnailSize: Boolean = true,
    contentScale: ContentScale = ContentScale.Crop
) {
    var state by remember(url) { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }
    val colors = LumiformColors.current

    Box(
        modifier = modifier
            .then(if (thumbnailSize) Modifier.width(52.dp).height(52.dp) else Modifier)
            .clip(RoundedCornerShape(if (thumbnailSize) 14.dp else 0.dp))
    ) {
        when (state) {
            is AsyncImagePainter.State.Loading, AsyncImagePainter.State.Empty ->
                Box(modifier = Modifier.fillMaxSize().shimmerPlaceholder())

            is AsyncImagePainter.State.Error ->
                Box(
                    modifier = Modifier.fillMaxSize().background(colors.errorContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Filled.BrokenImage, contentDescription = "Failed to load image", tint = colors.onErrorContainer)
                }

            else -> Unit
        }

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(url).crossfade(true).build(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            onState = { state = it },
            modifier = Modifier.fillMaxSize()
        )
    }
}