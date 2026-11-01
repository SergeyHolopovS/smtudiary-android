package com.smtudiary.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.smtudiary.android.data.repository.SettingsRepository
import com.smtudiary.android.ui.groups.GroupsScreen
import com.smtudiary.android.ui.home.HomeScreen
import com.smtudiary.android.ui.loading.LoadingScreen
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Serializable data object ChooseGroupRoute

@Serializable data class HomeRoute(val group: String)

@Composable
fun AppNavigation(
    viewModel: NavigationViewModel = hiltViewModel()
) {
    val savedGroup by viewModel.savedGroup.collectAsState()

    val group = savedGroup

    val navController = rememberNavController()

    if (group == null) {
        LoadingScreen()
        return
    }

    val startDestination: Any =
        if (group.isNotBlank()) {
            HomeRoute(group)
        } else {
            ChooseGroupRoute
        }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<HomeRoute> {
            HomeScreen()
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