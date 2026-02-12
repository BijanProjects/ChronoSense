package com.chronosense.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chronosense.ui.screens.day.DayScreen
import com.chronosense.ui.screens.entry.EntryScreen
import com.chronosense.ui.screens.month.MonthScreen
import com.chronosense.ui.screens.settings.SettingsScreen

@Composable
fun ChronoNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavScreens = listOf(Screen.Day, Screen.Month, Screen.Settings)
    val showBottomBar = currentRoute in bottomNavScreens.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    bottomNavScreens.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Day.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon!! else screen.unselectedIcon!!,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Day.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                    initialOffsetX = { 30 },
                    animationSpec = tween(300)
                )
            },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            composable(Screen.Day.route) {
                DayScreen(
                    onTimeSlotClick = { date, startTime, endTime ->
                        navController.navigate(
                            Screen.Entry.createRoute(
                                date.toString(),
                                startTime.toString(),
                                endTime.toString()
                            )
                        )
                    }
                )
            }

            composable(Screen.Month.route) {
                MonthScreen(
                    onDayClick = { _ ->
                        navController.navigate(Screen.Day.route)
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen()
            }

            composable(
                route = Screen.Entry.route,
                arguments = listOf(
                    navArgument("date") { type = NavType.StringType },
                    navArgument("startTime") { type = NavType.StringType },
                    navArgument("endTime") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val date = backStackEntry.arguments?.getString("date") ?: ""
                val startTime = backStackEntry.arguments?.getString("startTime") ?: ""
                val endTime = backStackEntry.arguments?.getString("endTime") ?: ""

                EntryScreen(
                    date = date,
                    startTime = startTime,
                    endTime = endTime,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
