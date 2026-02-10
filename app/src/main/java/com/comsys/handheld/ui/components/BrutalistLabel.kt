package com.comsys.handheld.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.comsys.handheld.ui.theme.BrutalistColors
import com.comsys.handheld.ui.theme.BrutalistTypography
import com.comsys.handheld.ui.theme.brutalistBorder

/**
 * Brutalist read-only text box component
 *
 * Features:
 * - Heavy black borders
 * - Stark color contrasts
 * - Bold typography
 * - Read-only display
 *
 * @param text The text to display
 * @param modifier Modifier for the component
 * @param backgroundColor Background color of the text box
 * @param textColor Color of the text
 * @param borderColor Color of the border
 */
@Composable
fun BrutalistTextBox(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = BrutalistColors.White,
    textColor: Color = BrutalistColors.Black,
    borderColor: Color = BrutalistColors.Black
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .brutalistBorder(borderColor)
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            style = BrutalistTypography.ButtonLabel,
            color = textColor
        )
    }
}

/**
 * Brutalist text box with label
 *
 * @param label The label text
 * @param text The content text
 * @param modifier Modifier for the component
 */
@Composable
fun BrutalistLabeledTextBox(
    label: String,
    text: String,
    modifier: Modifier = Modifier,
    labelBackgroundColor: Color = BrutalistColors.BrightYellow,
    textBackgroundColor: Color = BrutalistColors.White
) {
    Column(modifier = modifier) {
        // Label
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(labelBackgroundColor)
                .brutalistBorder()
                .padding(8.dp)
        ) {
            Text(
                text = label.uppercase(),
                style = BrutalistTypography.NavLabel,
                color = BrutalistColors.Black
            )
        }

        // Text content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(textBackgroundColor)
                .brutalistBorder()
                .padding(16.dp)
        ) {
            Text(
                text = text,
                style = BrutalistTypography.ButtonLabel,
                color = BrutalistColors.Black
            )
        }
    }
}

/**
 * Brutalist multiline text box
 *
 * @param text The text to display
 * @param modifier Modifier for the component
 * @param minLines Minimum number of lines
 */
@Composable
fun BrutalistMultilineTextBox(
    text: String,
    modifier: Modifier = Modifier,
    minLines: Int = 3,
    backgroundColor: Color = BrutalistColors.White,
    textColor: Color = BrutalistColors.Black
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .border(
                width = 3.dp,
                color = BrutalistColors.Black
            )
            .padding(16.dp)
    ) {
        Text(
            text = text,
            style = BrutalistTypography.ButtonLabel,
            color = textColor,
            minLines = minLines,
            textAlign = TextAlign.Start
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BrutalistTextBoxPreview() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        BrutalistTextBox(
            text = "EPC: 3000 1234 5678 90AB CDEF"
        )

        Box(modifier = Modifier.padding(16.dp))

        BrutalistLabeledTextBox(
            label = "Tag ID",
            text = "3000 1234 5678 90AB CDEF"
        )

        Box(modifier = Modifier.padding(16.dp))

        BrutalistMultilineTextBox(
            text = "This is a multiline text box\nwith brutalist styling\nand bold design",
            minLines = 3
        )
    }
}