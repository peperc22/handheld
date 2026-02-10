package com.comsys.handheld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.comsys.handheld.ui.components.BrutalistBottomNavigation
import com.comsys.handheld.ui.components.BrutalistNavItem
import com.comsys.handheld.ui.theme.HandheldTheme
import com.comsys.handheld.uhf.AccessControlScreen
import com.comsys.handheld.uhf.UHFMenuScreen
import com.comsys.handheld.uhf.UHFScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HandheldTheme {
                HandheldApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun HandheldApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.UHF_READER) }
    var uhfScreen by rememberSaveable { mutableStateOf(UHFScreens.MENU) }

    val navItems = AppDestinations.entries.map { destination ->
        BrutalistNavItem(
            icon = destination.icon,
            label = destination.label,
            route = destination.name
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BrutalistBottomNavigation(
                items = navItems,
                selectedRoute = currentDestination.name,
                onItemClick = { route ->
                    currentDestination = AppDestinations.valueOf(route)
                    // Reset to menu when switching to UHF_READER
                    if (currentDestination == AppDestinations.UHF_READER) {
                        uhfScreen = UHFScreens.MENU
                    }
                },
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { innerPadding ->
        when (currentDestination) {
            AppDestinations.UHF_READER -> {
                when (uhfScreen) {
                    UHFScreens.MENU -> UHFMenuScreen(
                        onAccessControlClick = {
                            uhfScreen = UHFScreens.ACCESS_CONTROL
                        },
                        onRFIDScanClick = {
                            uhfScreen = UHFScreens.SCAN
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                    UHFScreens.ACCESS_CONTROL -> AccessControlScreen(
                        onBackClick = { uhfScreen = UHFScreens.MENU },
                        modifier = Modifier.padding(innerPadding)
                    )
                    UHFScreens.SCAN -> UHFScreen(
                        onBackClick = { uhfScreen = UHFScreens.MENU },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
            AppDestinations.FAVORITES -> Greeting(
                name = "Ajustes",
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

enum class UHFScreens {
    MENU,
    ACCESS_CONTROL,
    SCAN
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    UHF_READER("UHF Reader", Icons.Filled.Star),
    FAVORITES("Ajustes", Icons.Filled.Settings),
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HandheldTheme {
        Greeting("Android")
    }
}