package com.smtudiary.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smtudiary.android.ui.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable data class HomeRoute(val group: String)

@Composable
fun AppNavigation() {
    NavHost(
        navController = rememberNavController(),
        startDestination = HomeRoute("20250")
    ) {
        composable<HomeRoute> {
            HomeScreen()
        }
    }
}