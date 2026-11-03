package com.smtudiary.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smtudiary.android.ui.groups.GroupsScreen
import com.smtudiary.android.ui.home.HomeScreen
import com.smtudiary.android.ui.loading.LoadingScreen
import com.smtudiary.android.ui.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable data object ChooseGroupRoute

@Serializable data object SettingsRoute

@Serializable data class HomeRoute(val group: String)

@Composable
fun AppNavigation(
    viewModel: NavigationViewModel = hiltViewModel()
) {
    val group by viewModel.savedGroup.collectAsStateWithLifecycle()

    val navController = rememberNavController()

    val savedGroup = group
    if (savedGroup == null) {
        LoadingScreen()
        return
    }

    val startDestination: Any =
        if (savedGroup.isNotBlank()) {
            HomeRoute(savedGroup)
        } else {
            ChooseGroupRoute
        }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<HomeRoute> {
            HomeScreen(
                onNavigateSettings = {
                    navController.navigate(SettingsRoute){
                        popUpTo<HomeRoute> {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<SettingsRoute> {
            SettingsScreen { currentGroup ->
                navController.navigate(HomeRoute(currentGroup)) {
                    popUpTo<SettingsRoute> {
                        inclusive = true
                    }
                }
            }
        }

        composable<ChooseGroupRoute> {
            GroupsScreen(
                onGroupSelected = { selectedGroup ->
                    navController.navigate(HomeRoute(selectedGroup)) {
                        popUpTo<ChooseGroupRoute> {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
