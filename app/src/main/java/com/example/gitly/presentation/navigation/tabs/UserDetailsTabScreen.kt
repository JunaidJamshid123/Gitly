package com.example.gitly.presentation.navigation.tabs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.gitly.presentation.ui.screens.user_detail.UserDetailScreen

@Composable
fun UserDetailsTabScreen(navController: NavHostController) {
    // Use the existing UserDetailScreen which has search functionality
    UserDetailScreen(navController = navController)
}
