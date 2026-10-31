package com.sid.tapedeck.ui.components

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.sid.tapedeck.ui.theme.TapeTheme
import com.sid.tapedeck.ui.theme.chunkyBorder
import kotlin.math.abs

/**
 * AlbumArt: shows real cover art with Coil from MediaStore album art Uri,
 * downsampled to about 300px with memory caching.
 * If missing or load fails, gracefully falls back to the generated Canvas tile.
 */
@Composable
fun AlbumArt(
    albumId: String?,
    artworkUri: Any?,
    modifier: Modifier = Modifier,
    size: Dp = 46.dp
) {
    val colors = TapeTheme.colors
    val cornerRadius = size * 0.16f
    val shape = RoundedCornerShape(cornerRadius)

    val hasValidUri = when (artworkUri) {
        null -> false
        is String -> artworkUri.isNotBlank()
        is Uri -> artworkUri.toString().isNotBlank()
        else -> true
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .chunkyBorder(colors.line, width = 3.dp, shape = shape)
    ) {
        if (hasValidUri) {
            val context = LocalContext.current
            var isLoadFailed by remember(artworkUri) { mutableStateOf(false) }

            if (!isLoadFailed) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(artworkUri)
                        .size(300) // Downsample to ~300px for low-memory handheld profile
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .crossfade(false) // Low-end phone optimization
                        .build(),
                    contentDescription = "Album Art",
                    contentScale = ContentScale.Crop,
                    onError = { isLoadFailed = true },
                    modifier = Modifier.size(size)
                )
            } else {
                AlbumArtFallbackTile(
                    albumId = albumId ?: "tape_default",
                    size = size
                )
            }
        } else {
            AlbumArtFallbackTile(
                albumId = albumId ?: "tape_default",
                size = size
            )
        }
    }
}

@Composable
fun AlbumArtFallbackTile(
    albumId: String,
    size: Dp
) {
    val colors = TapeTheme.colors
    val hash = abs(albumId.hashCode())
    val groundColors = listOf(colors.lime, colors.yellow, colors.rose)
    val groundColor = groundColors[hash % groundColors.size]
    val layoutIndex = (hash / 3) % 3

    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Fill background ground color
        drawRect(color = groundColor)

        val blockRadius = CornerRadius(w * 0.08f, h * 0.08f)

        when (layoutIndex) {
            0 -> {
                // Layout 0: Two top blocks + one wide bottom block
                drawRoundRect(
                    color = colors.lime2,
                    topLeft = Offset(w * 0.12f, h * 0.12f),
                    size = Size(w * 0.34f, h * 0.34f),
                    cornerRadius = blockRadius
                )
                drawRoundRect(
                    color = colors.lime3,
                    topLeft = Offset(w * 0.54f, h * 0.12f),
                    size = Size(w * 0.34f, h * 0.34f),
                    cornerRadius = blockRadius
                )
                drawRoundRect(
                    color = colors.lime4,
                    topLeft = Offset(w * 0.12f, h * 0.54f),
                    size = Size(w * 0.76f, h * 0.34f),
                    cornerRadius = blockRadius
                )
            }
            1 -> {
                // Layout 1: Tall left block + two stacked right blocks
                drawRoundRect(
                    color = colors.lime3,
                    topLeft = Offset(w * 0.12f, h * 0.12f),
                    size = Size(w * 0.34f, h * 0.76f),
                    cornerRadius = blockRadius
                )
                drawRoundRect(
                    color = colors.lime4,
                    topLeft = Offset(w * 0.54f, h * 0.12f),
                    size = Size(w * 0.34f, h * 0.34f),
                    cornerRadius = blockRadius
                )
                drawRoundRect(
                    color = colors.lime2,
                    topLeft = Offset(w * 0.54f, h * 0.54f),
                    size = Size(w * 0.34f, h * 0.34f),
                    cornerRadius = blockRadius
                )
            }
            else -> {
                // Layout 2: Three staggered horizontal bars
                drawRoundRect(
                    color = colors.lime4,
                    topLeft = Offset(w * 0.12f, h * 0.14f),
                    size = Size(w * 0.76f, h * 0.20f),
                    cornerRadius = blockRadius
                )
                drawRoundRect(
                    color = colors.lime2,
                    topLeft = Offset(w * 0.22f, h * 0.40f),
                    size = Size(w * 0.66f, h * 0.20f),
                    cornerRadius = blockRadius
                )
                drawRoundRect(
                    color = colors.lime3,
                    topLeft = Offset(w * 0.12f, h * 0.66f),
                    size = Size(w * 0.60f, h * 0.20f),
                    cornerRadius = blockRadius
                )
            }
        }
    }
}
