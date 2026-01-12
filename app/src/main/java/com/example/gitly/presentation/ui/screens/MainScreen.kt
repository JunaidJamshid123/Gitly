package com.example.gitly.presentation.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
import com.example.gitly.presentation.ui.screens.about.AboutScreen
import com.example.gitly.presentation.ui.screens.favorites.FavoritesScreen
import com.example.gitly.presentation.ui.screens.help.HelpScreen
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
                    scope.launch {
                        drawerState.close()
                    }
                    // Handle drawer item clicks
                    when (item) {
                        DrawerItem.RateUs -> {
                            rateApp(context)
                        }
                        DrawerItem.ShareApp -> {
                            shareApp(context)
                        }
                        DrawerItem.About -> {
                            bottomNavController.navigate("about")
                        }
                        DrawerItem.Help -> {
                            bottomNavController.navigate("help")
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
                composable(BottomNavItem.Home.route) { 
                    HomeScreen(bottomNavController) 
                }
                composable(BottomNavItem.UserDetails.route) { 
                    UserDetailScreen(bottomNavController)
                }
                composable(BottomNavItem.RepoDetails.route) { 
                    RepoDetailScreen(bottomNavController)
                }
                composable(BottomNavItem.AiInsights.route) { 
                    AI_InsightScreeen(bottomNavController)
                }
                composable(BottomNavItem.Favorites.route) { 
                    FavoritesScreen(bottomNavController) 
                }
                
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
                
                // About Screen
                composable("about") {
                    AboutScreen(bottomNavController)
                }
                
                // Help & Support Screen
                composable("help") {
                    HelpScreen(bottomNavController)
                }
            }
        }
    }
}

// Helper function to rate app
private fun rateApp(context: android.content.Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("market://details?id=${context.packageName}")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to browser
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
        }
        context.startActivity(intent)
    }
}

// Helper function to share app
private fun shareApp(context: android.content.Context) {
    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out Gitly!")
            putExtra(
                Intent.EXTRA_TEXT,
                "Discover GitHub like never before with Gitly! 🚀\n\n" +
                        "Explore trending repositories, find talented developers, and get AI-powered insights.\n\n" +
                        "Download now: https://play.google.com/store/apps/details?id=${context.packageName}"
            )
        }
        context.startActivity(Intent.createChooser(intent, "Share Gitly"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}