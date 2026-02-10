package com.comsys.handheld.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.comsys.handheld.ui.theme.BrutalistColors
import com.comsys.handheld.ui.theme.BrutalistTypography
import com.comsys.handheld.ui.theme.brutalistBorder

/**
 * Brutalist menu button component
 *
 * Features:
 * - Large icon at the top
 * - Title text below icon
 * - Subtitle text below title
 * - Transparent background normally
 * - Highlights when pressed
 * - Heavy black borders
 *
 * @param icon The icon to display
 * @param title The main title text
 * @param subtitle The subtitle text
 * @param onClick Callback when button is clicked
 * @param modifier Modifier for the component
 * @param pressedBackgroundColor Background color when pressed
 */
@Composable
fun BrutalistMenuButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    pressedBackgroundColor: Color = BrutalistColors.BrightYellow
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (isPressed) pressedBackgroundColor else Color.Transparent
            )
            .brutalistBorder()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(64.dp),
                tint = BrutalistColors.Black
            )

            // Title
            Text(
                text = title.uppercase(),
                style = BrutalistTypography.Header,
                color = BrutalistColors.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )

            // Subtitle
            Text(
                text = subtitle.uppercase(),
                style = BrutalistTypography.NavLabel,
                color = BrutalistColors.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BrutalistMenuButtonPreview() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BrutalistMenuButton(
            icon = Icons.Filled.Person,
            title = "ACCESOS",
            subtitle = "REGISTROS DE ENTRADA Y SALIDA",
            onClick = {}
        )

        BrutalistMenuButton(
            icon = Icons.Filled.Star,
            title = "RFID",
            subtitle = "ESCANEO DE ETIQUETAS UHF",
            onClick = {}
        )
    }
}
