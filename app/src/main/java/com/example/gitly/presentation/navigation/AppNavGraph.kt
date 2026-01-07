package com.example.gitly.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gitly.SplashScreen
import com.example.gitly.presentation.ui.screens.repo_details.RepoDetailsScreen
import com.example.gitly.presentation.ui.screens.statistics.RepoStatisticsScreen
import com.example.gitly.presentation.ui.screens.statistics.UserStatisticsScreen
import com.example.gitly.presentation.ui.screens.user_detail.UserProfileDetailScreen

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
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            UserProfileDetailScreen(navController = navController, username = username)
        }
        
        // Repository Detail Screen
        composable(
            route = Routes.REPO_DETAIL,
            arguments = listOf(
                navArgument("owner") { type = NavType.StringType },
                navArgument("repo") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val owner = backStackEntry.arguments?.getString("owner") ?: ""
            val repo = backStackEntry.arguments?.getString("repo") ?: ""
            RepoDetailsScreen(owner = owner, repo = repo, navController = navController)
        }
        
        // User Statistics Screen
        composable(
            route = Routes.USER_STATISTICS,
            arguments = listOf(
                navArgument("username") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            UserStatisticsScreen(navController = navController, username = username)
        }
        
        // Repository Statistics Screen
        composable(
            route = Routes.REPO_STATISTICS,
            arguments = listOf(
                navArgument("owner") { type = NavType.StringType },
                navArgument("repo") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val owner = backStackEntry.arguments?.getString("owner") ?: ""
            val repo = backStackEntry.arguments?.getString("repo") ?: ""
            RepoStatisticsScreen(navController = navController, owner = owner, repoName = repo)
        }
    }
}
