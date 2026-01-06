package com.example.gitly.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gitly.SplashScreen
import com.example.gitly.presentation.ui.screens.repo_detail.RepoDetailScreen
import com.example.gitly.presentation.ui.screens.user_detail.UserDetailScreen

/**
 * Root navigation graph for the app.
 */
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        // Splash Screen
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        
        // Main Screen with Bottom Navigation
        composable(Routes.MAIN) {
            MainScreen(rootNavController = navController)
        }
        
        // User Detail Screen
        composable(
            route = Routes.USER_DETAIL,
            arguments = listOf(
                navArgument("username") { type = NavType.StringType }
            )
        ) {
            UserDetailScreen(navController = navController)
        }
        
        // Repository Detail Screen
        composable(
            route = Routes.REPO_DETAIL,
            arguments = listOf(
                navArgument("owner") { type = NavType.StringType },
                navArgument("repo") { type = NavType.StringType }
            )
        ) {
            RepoDetailScreen(navController = navController)
        }
    }
}
