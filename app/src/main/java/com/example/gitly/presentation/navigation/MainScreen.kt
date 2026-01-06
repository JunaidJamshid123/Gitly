package com.example.gitly.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gitly.presentation.ui.screens.ai_insights.AiInsightsScreen
import com.example.gitly.presentation.ui.screens.favorites.FavoritesScreen
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
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerContent(
                onItemClick = { item ->
                    // Handle drawer item clicks
                    when (item) {
                        DrawerItem.Settings -> { /* Handle settings */ }
                        DrawerItem.SavedItems -> { /* Handle saved items */ }
                        DrawerItem.History -> { /* Handle history */ }
                        DrawerItem.OfflineMode -> { /* Handle offline mode */ }
                        DrawerItem.RateUs -> { /* Handle rate us */ }
                        DrawerItem.ShareApp -> { /* Handle share app */ }
                        DrawerItem.About -> { /* Handle about */ }
                        DrawerItem.Help -> { /* Handle help */ }
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
                    AiInsightsScreen()
                }
                
                composable(Routes.FAVORITES) {
                    FavoritesScreen(navController = rootNavController)
                }
            }
        }
    }
}
