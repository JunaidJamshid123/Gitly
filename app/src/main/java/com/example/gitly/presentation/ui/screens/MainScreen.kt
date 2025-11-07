package com.example.gitly.presentation.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gitly.presentation.navigation.BottomNavBar
import com.example.gitly.presentation.navigation.BottomNavItem
import com.example.gitly.presentation.navigation.DrawerItem
import com.example.gitly.presentation.navigation.NavigationDrawerContent
import com.example.gitly.presentation.navigation.TopBar
import com.example.gitly.presentation.ui.screens.AI_Insights.AI_InsightScreeen
import com.example.gitly.presentation.ui.screens.favorites.FavoritesScreen
import com.example.gitly.presentation.ui.screens.home.HomeScreen
import com.example.gitly.presentation.ui.screens.repo_detail.RepoDetailScreen
import com.example.gitly.presentation.ui.screens.repo_details.RepoDetailsScreen
import com.example.gitly.presentation.ui.screens.statistics.RepoStatisticsScreen
import com.example.gitly.presentation.ui.screens.statistics.UserStatisticsScreen
import com.example.gitly.presentation.ui.screens.user_detail.UserDetailScreen
import com.example.gitly.presentation.ui.screens.user_detail.UserProfileDetailScreen
import kotlinx.coroutines.launch

@Composable
fun MainScreen(navController: NavHostController) {
    // Create a separate nav controller for bottom navigation
    val bottomNavController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerContent(
                onItemClick = { item ->
                    // Handle drawer item clicks
                    when (item) {
                        DrawerItem.Settings -> {
                            Toast.makeText(context, "Settings clicked", Toast.LENGTH_SHORT).show()
                        }
                        DrawerItem.SavedItems -> {
                            Toast.makeText(context, "Saved Items clicked", Toast.LENGTH_SHORT).show()
                        }
                        DrawerItem.History -> {
                            Toast.makeText(context, "History clicked", Toast.LENGTH_SHORT).show()
                        }
                        DrawerItem.OfflineMode -> {
                            Toast.makeText(context, "Offline Mode clicked", Toast.LENGTH_SHORT).show()
                        }
                        DrawerItem.RateUs -> {
                            Toast.makeText(context, "Rate Us clicked", Toast.LENGTH_SHORT).show()
                        }
                        DrawerItem.ShareApp -> {
                            Toast.makeText(context, "Share App clicked", Toast.LENGTH_SHORT).show()
                        }
                        DrawerItem.About -> {
                            Toast.makeText(context, "About clicked", Toast.LENGTH_SHORT).show()
                        }
                        DrawerItem.Help -> {
                            Toast.makeText(context, "Help & Support clicked", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onClose = {
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                TopBar(
                    onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            },
            bottomBar = { BottomNavBar(bottomNavController) }
        ) { innerPadding ->
            NavHost(
                navController = bottomNavController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier
                    .padding(innerPadding)
                    .background(Color.White)
            ) {
                composable(BottomNavItem.Home.route) { HomeScreen(bottomNavController) }
                composable(BottomNavItem.UserDetails.route) { UserDetailScreen(bottomNavController) }
                composable(BottomNavItem.RepoDetails.route) { RepoDetailScreen(bottomNavController) }
                composable(BottomNavItem.AiInsights.route) { AI_InsightScreeen(bottomNavController) }
                composable(BottomNavItem.Favorites.route) { FavoritesScreen(bottomNavController) }
                
                // Repository Details Screen
                composable(
                    route = "repo_details/{owner}/{repo}",
                    arguments = listOf(
                        navArgument("owner") { type = NavType.StringType },
                        navArgument("repo") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val owner = backStackEntry.arguments?.getString("owner") ?: ""
                    val repo = backStackEntry.arguments?.getString("repo") ?: ""
                    RepoDetailsScreen(
                        owner = owner,
                        repo = repo,
                        navController = bottomNavController
                    )
                }
                
                // User Profile Detail Screen
                composable(
                    route = "user_profile/{username}",
                    arguments = listOf(navArgument("username") { type = NavType.StringType })
                ) { backStackEntry ->
                    val username = backStackEntry.arguments?.getString("username") ?: ""
                    UserProfileDetailScreen(bottomNavController, username)
                }
                
                // User Statistics Screen
                composable(
                    route = "user_statistics/{username}",
                    arguments = listOf(navArgument("username") { type = NavType.StringType })
                ) { backStackEntry ->
                    val username = backStackEntry.arguments?.getString("username") ?: ""
                    UserStatisticsScreen(bottomNavController, username)
                }
                
                // Repository Statistics Screen
                composable(
                    route = "repo_statistics/{owner}/{repo}",
                    arguments = listOf(
                        navArgument("owner") { type = NavType.StringType },
                        navArgument("repo") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val owner = backStackEntry.arguments?.getString("owner") ?: ""
                    val repo = backStackEntry.arguments?.getString("repo") ?: ""
                    RepoStatisticsScreen(bottomNavController, owner, repo)
                }
            }
        }
    }
}

