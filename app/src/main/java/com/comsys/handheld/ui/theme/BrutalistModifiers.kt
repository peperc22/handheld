package com.comsys.handheld.ui.theme

import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Size

/**
 * Brutalist style with regular border and offset shadow
 * - Regular border around the element
 * - Shadow strips on bottom and right edges only
 * - Creates classic brutalist 3D effect
 */
fun Modifier.brutalistBorder(
    borderColor: Color = BrutalistColors.Black,
    shadowColor: Color = BrutalistColors.Black,
    borderWidth: Dp = 2.dp,
    shadowOffset: Dp = 5.dp
): Modifier = this.drawWithContent {
    val borderPx = borderWidth.toPx()
    val offsetPx = shadowOffset.toPx()

    // Draw the content first (background + element content)
    drawContent()

    // Draw shadow strips AFTER content (visible on bottom and right)

    // Bottom shadow strip
    drawRect(
        color = shadowColor,
        topLeft = Offset(offsetPx, size.height),
        size = Size(size.width, offsetPx)
    )

    // Right shadow strip
    drawRect(
        color = shadowColor,
        topLeft = Offset(size.width, offsetPx),
        size = Size(offsetPx, size.height)
    )

    // Draw border on top of everything
    // Top border
    drawRect(
        color = borderColor,
        topLeft = Offset(0f, 0f),
        size = Size(size.width, borderPx)
    )
    // Left border
    drawRect(
        color = borderColor,
        topLeft = Offset(0f, 0f),
        size = Size(borderPx, size.height)
    )
    // Bottom border
    drawRect(
        color = borderColor,
        topLeft = Offset(0f, size.height - borderPx),
        size = Size(size.width, borderPx)
    )
    // Right border
    drawRect(
        color = borderColor,
        topLeft = Offset(size.width - borderPx, 0f),
        size = Size(borderPx, size.height)
    )
}

/**
 * Uniform brutalist border (all sides same thickness)
 * Use this for elements that don't need the shadow effect
 */
fun Modifier.brutalistBorderUniform(
    color: Color = BrutalistColors.Black,
    width: Dp = 3.dp
): Modifier = this.border(width = width, color = color)
