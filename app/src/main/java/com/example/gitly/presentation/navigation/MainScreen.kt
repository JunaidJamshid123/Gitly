package com.example.gitly.presentation.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gitly.presentation.ui.screens.AI_Insights.AI_InsightScreeen
import com.example.gitly.presentation.ui.screens.about.AboutScreen
import com.example.gitly.presentation.ui.screens.favorites.FavoritesScreen
import com.example.gitly.presentation.ui.screens.help.HelpScreen
import com.example.gitly.presentation.ui.screens.home.HomeScreen
import com.example.gitly.presentation.navigation.tabs.RepoDetailsTabScreen
import com.example.gitly.presentation.navigation.tabs.UserDetailsTabScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    rootNavController: NavHostController
) {
    val navController = rememberNavController()
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
                            navController.navigate("about")
                        }
                        DrawerItem.Help -> {
                            navController.navigate("help")
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
            topBar = {
                TopBar(
                    onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavBar(navController = navController)
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Routes.HOME) {
                    HomeScreen(navController = rootNavController)
                }
                
                composable("repo_details") {
                    RepoDetailsTabScreen(navController = rootNavController)
                }
                
                composable("user_details") {
                    UserDetailsTabScreen(navController = rootNavController)
                }
                
                composable("ai_insights") {
                    AI_InsightScreeen(rootNavController)
                }
                
                composable(Routes.FAVORITES) {
                    FavoritesScreen(navController = rootNavController)
                }
                
                // About Screen
                composable("about") {
                    AboutScreen(navController)
                }
                
                // Help & Support Screen
                composable("help") {
                    HelpScreen(navController)
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
