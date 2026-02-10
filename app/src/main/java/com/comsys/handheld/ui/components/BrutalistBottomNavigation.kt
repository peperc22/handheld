package com.comsys.handheld.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import com.comsys.handheld.ui.theme.BrutalistColors
import com.comsys.handheld.ui.theme.BrutalistTypography
import com.comsys.handheld.ui.theme.brutalistBorder

/**
 * Data class representing a navigation item
 */
data class BrutalistNavItem(
    val icon: ImageVector,
    val label: String,
    val route: String
)

/**
 * Brutalism-style bottom navigation bar
 *
 * Features:
 * - Heavy black borders (4dp)
 * - Stark color contrasts
 * - Bold, geometric shapes
 * - No shadows or gradients
 * - Raw, unpolished aesthetic
 *
 * @param items List of navigation items
 * @param selectedRoute Currently selected route
 * @param onItemClick Callback when an item is clicked
 * @param modifier Modifier for the navigation bar
 * @param backgroundColor Background color of the navigation bar
 * @param selectedColor Color for selected items
 * @param unselectedColor Color for unselected items
 */
@Composable
fun BrutalistBottomNavigation(
    items: List<BrutalistNavItem>,
    selectedRoute: String,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = BrutalistColors.White,
    selectedColor: Color = BrutalistColors.BrightYellow,
    unselectedColor: Color = BrutalistColors.Black
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .border(
                width = 4.dp,
                color = BrutalistColors.Black
            )
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            BrutalistNavItem(
                item = item,
                isSelected = item.route == selectedRoute,
                onClick = { onItemClick(item.route) },
                selectedColor = selectedColor,
                unselectedColor = unselectedColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Individual navigation item with brutalist styling
 */
@Composable
private fun BrutalistNavItem(
    item: BrutalistNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color,
    unselectedColor: Color,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) selectedColor else Color.Transparent
    val contentColor = if (isSelected) BrutalistColors.Black else unselectedColor

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .brutalistBorder()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            modifier = Modifier.size(28.dp),
            tint = contentColor
        )

        Text(
            text = item.label.uppercase(),
            style = BrutalistTypography.NavLabel,
            color = contentColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Alternative brutalist bottom navigation with full-width selected indicator
 */
@Composable
fun BrutalistBottomNavigationAlt(
    items: List<BrutalistNavItem>,
    selectedRoute: String,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BrutalistColors.Black)
    ) {
        // Top border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BrutalistColors.BrightRed)
                .padding(vertical = 3.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BrutalistColors.White)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                BrutalistNavItemAlt(
                    item = item,
                    isSelected = item.route == selectedRoute,
                    onClick = { onItemClick(item.route) }
                )
            }
        }
    }
}

@Composable
private fun BrutalistNavItemAlt(
    item: BrutalistNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(
                if (isSelected) BrutalistColors.BrightRed else Color.Transparent
            )
            .border(
                width = 4.dp,
                color = BrutalistColors.Black
            )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(32.dp),
                tint = BrutalistColors.Black
            )

            Text(
                text = item.label.uppercase(),
                style = BrutalistTypography.NavLabel,
                color = BrutalistColors.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
private fun BrutalistBottomNavigationPreview() {
    val items = listOf(
        BrutalistNavItem(Icons.Filled.Home, "Home", "home"),
        BrutalistNavItem(Icons.Filled.Star, "Star", "star"),
        BrutalistNavItem(Icons.Filled.Favorite, "Fav", "favorite")
    )

    Column {
        BrutalistBottomNavigation(
            items = items,
            selectedRoute = "home",
            onItemClick = {}
        )

        Box(modifier = Modifier.padding(16.dp))

        BrutalistBottomNavigationAlt(
            items = items,
            selectedRoute = "star",
            onItemClick = {}
        )
    }
}
