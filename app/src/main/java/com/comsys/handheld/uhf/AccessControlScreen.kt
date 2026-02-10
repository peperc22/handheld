package com.comsys.handheld.uhf

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.comsys.handheld.ui.theme.BrutalistColors
import com.comsys.handheld.ui.theme.BrutalistTypography
import com.comsys.handheld.ui.theme.brutalistBorder

/**
 * Access Control Screen - Placeholder for future implementation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessControlScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ACCESOS",
                        style = BrutalistTypography.Header
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BrutalistColors.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrutalistColors.BrightYellow,
                    titleContentColor = BrutalistColors.Black
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "REGISTROS DE ENTRADA Y SALIDA\n(EN DESARROLLO)",
                style = BrutalistTypography.Header,
                color = BrutalistColors.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AccessControlScreenPreview() {
    AccessControlScreen(onBackClick = {})
}
