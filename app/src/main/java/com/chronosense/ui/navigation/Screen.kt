package com.chronosense.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Today
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {
    data object Day : Screen(
        route = "day",
        title = "Today",
        selectedIcon = Icons.Filled.Today,
        unselectedIcon = Icons.Outlined.Today
    )

    data object Month : Screen(
        route = "month",
        title = "Month",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth
    )

    data object Settings : Screen(
        route = "settings",
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    data object Entry : Screen(
        route = "entry/{date}/{startTime}/{endTime}",
        title = "Entry"
    ) {
        fun createRoute(date: String, startTime: String, endTime: String): String {
            return "entry/$date/$startTime/$endTime"
        }
    }
}
