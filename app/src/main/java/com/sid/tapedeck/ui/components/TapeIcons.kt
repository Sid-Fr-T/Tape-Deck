package com.sid.tapedeck.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * 24dp, 2.4dp stroke, round cap & join custom icons for Tapedeck.
 * Zero external icon dependency, completely offline.
 */
object TapeIcons {
    private const val STROKE_WIDTH = 2.4f

    private fun strokeIcon(
        name: String,
        builder: PathBuilder.() -> Unit
    ): ImageVector = ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.White), // Tinted dynamically at runtime
        strokeLineWidth = STROKE_WIDTH,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        builder()
    }.build()

    val Search: ImageVector = strokeIcon("Search") {
        moveTo(17f, 10.5f)
        arcTo(6.5f, 6.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 4f, 10.5f)
        arcTo(6.5f, 6.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 17f, 10.5f)
        moveTo(15.2f, 15.2f)
        lineTo(20.5f, 20.5f)
    }

    val Back: ImageVector = strokeIcon("Back") {
        moveTo(19f, 12f)
        lineTo(5f, 12f)
        moveTo(12f, 5f)
        lineTo(5f, 12f)
        lineTo(12f, 19f)
    }

    val Down: ImageVector = strokeIcon("Down") {
        moveTo(6f, 9f)
        lineTo(12f, 15f)
        lineTo(18f, 9f)
    }

    val Chevron: ImageVector = strokeIcon("Chevron") {
        moveTo(9f, 6f)
        lineTo(15f, 12f)
        lineTo(9f, 18f)
    }

    val Queue: ImageVector = strokeIcon("Queue") {
        moveTo(4f, 7f)
        lineTo(16f, 7f)
        moveTo(4f, 12f)
        lineTo(13f, 12f)
        moveTo(4f, 17f)
        lineTo(10f, 17f)
        moveTo(17f, 13f)
        lineTo(17f, 19f)
        moveTo(14f, 16f)
        lineTo(20f, 16f)
    }

    val Drag: ImageVector = strokeIcon("Drag") {
        moveTo(5f, 9f)
        lineTo(19f, 9f)
        moveTo(5f, 15f)
        lineTo(19f, 15f)
    }

    val More: ImageVector = strokeIcon("More") {
        moveTo(12f, 6f)
        lineTo(12f, 6.1f)
        moveTo(12f, 12f)
        lineTo(12f, 12.1f)
        moveTo(12f, 18f)
        lineTo(12f, 18.1f)
    }

    val Sort: ImageVector = strokeIcon("Sort") {
        moveTo(4f, 6f)
        lineTo(20f, 6f)
        moveTo(4f, 12f)
        lineTo(15f, 12f)
        moveTo(4f, 18f)
        lineTo(10f, 18f)
    }

    val SortArrows: ImageVector = strokeIcon("SortArrows") {
        moveTo(8f, 5f)
        lineTo(8f, 19f)
        moveTo(5f, 16f)
        lineTo(8f, 19f)
        lineTo(11f, 16f)
        moveTo(16f, 19f)
        lineTo(16f, 5f)
        moveTo(13f, 8f)
        lineTo(16f, 5f)
        lineTo(19f, 8f)
    }

    val Edit: ImageVector = strokeIcon("Edit") {
        moveTo(16.5f, 3.5f)
        lineTo(20.5f, 7.5f)
        lineTo(8f, 20f)
        lineTo(4f, 20f)
        lineTo(4f, 16f)
        close()
        moveTo(13.5f, 6.5f)
        lineTo(17.5f, 10.5f)
    }

    val Close: ImageVector = strokeIcon("Close") {
        moveTo(6f, 6f)
        lineTo(18f, 18f)
        moveTo(18f, 6f)
        lineTo(6f, 18f)
    }

    val Heart: ImageVector = strokeIcon("Heart") {
        moveTo(12f, 20f)
        curveTo(4.5f, 14f, 2.5f, 9.5f, 4.5f, 6.5f)
        curveTo(6.5f, 3.5f, 10.5f, 4.5f, 12f, 7f)
        curveTo(13.5f, 4.5f, 17.5f, 3.5f, 19.5f, 6.5f)
        curveTo(21.5f, 9.5f, 19.5f, 14f, 12f, 20f)
        close()
    }

    val Play: ImageVector = strokeIcon("Play") {
        moveTo(8f, 5.5f)
        lineTo(19f, 12f)
        lineTo(8f, 18.5f)
        close()
    }

    val Pause: ImageVector = strokeIcon("Pause") {
        moveTo(8f, 5.5f)
        lineTo(8f, 18.5f)
        moveTo(16f, 5.5f)
        lineTo(16f, 18.5f)
    }

    val Prev: ImageVector = strokeIcon("Prev") {
        moveTo(5f, 6f)
        lineTo(5f, 18f)
        moveTo(18f, 6f)
        lineTo(8f, 12f)
        lineTo(18f, 18f)
        close()
    }

    val Previous: ImageVector get() = Prev

    val Next: ImageVector = strokeIcon("Next") {
        moveTo(19f, 6f)
        lineTo(19f, 18f)
        moveTo(6f, 6f)
        lineTo(16f, 12f)
        lineTo(6f, 18f)
        close()
    }

    val Shuffle: ImageVector = strokeIcon("Shuffle") {
        moveTo(4f, 18f)
        lineTo(9f, 18f)
        lineTo(15f, 6f)
        lineTo(20f, 6f)
        moveTo(17f, 3.5f)
        lineTo(20f, 6f)
        lineTo(17f, 8.5f)
        moveTo(4f, 6f)
        lineTo(9f, 6f)
        lineTo(11f, 10f)
        moveTo(13f, 14f)
        lineTo(15f, 18f)
        lineTo(20f, 18f)
        moveTo(17f, 15.5f)
        lineTo(20f, 18f)
        lineTo(17f, 20.5f)
    }

    val Repeat: ImageVector = strokeIcon("Repeat") {
        moveTo(17f, 3.5f)
        lineTo(20f, 6.5f)
        lineTo(17f, 9.5f)
        moveTo(4f, 12f)
        lineTo(4f, 9.5f)
        curveTo(4f, 7.8f, 5.3f, 6.5f, 7f, 6.5f)
        lineTo(20f, 6.5f)
        moveTo(7f, 20.5f)
        lineTo(4f, 17.5f)
        lineTo(7f, 14.5f)
        moveTo(20f, 12f)
        lineTo(20f, 14.5f)
        curveTo(20f, 16.2f, 18.7f, 17.5f, 17f, 17.5f)
        lineTo(4f, 17.5f)
    }

    val Home: ImageVector = strokeIcon("Home") {
        moveTo(3.5f, 11f)
        lineTo(12f, 4f)
        lineTo(20.5f, 11f)
        moveTo(5.5f, 9.5f)
        lineTo(5.5f, 19.5f)
        lineTo(18.5f, 19.5f)
        lineTo(18.5f, 9.5f)
    }

    val Library: ImageVector = strokeIcon("Library") {
        moveTo(4f, 5.5f)
        lineTo(20f, 5.5f)
        lineTo(20f, 18.5f)
        lineTo(4f, 18.5f)
        close()
        moveTo(8.5f, 11f)
        lineTo(8.5f, 13f)
        moveTo(15.5f, 11f)
        lineTo(15.5f, 13f)
        moveTo(7f, 15.5f)
        lineTo(17f, 15.5f)
    }

    val User: ImageVector = strokeIcon("User") {
        moveTo(16f, 8f)
        arcTo(4f, 4f, 0f, isMoreThanHalf = true, isPositiveArc = true, 8f, 8f)
        arcTo(4f, 4f, 0f, isMoreThanHalf = true, isPositiveArc = true, 16f, 8f)
        moveTo(4.5f, 20f)
        curveTo(4.5f, 15.5f, 8f, 14.5f, 12f, 14.5f)
        curveTo(16f, 14.5f, 19.5f, 15.5f, 19.5f, 20f)
    }
}

