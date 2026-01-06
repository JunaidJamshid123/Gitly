package com.example.gitly.presentation.navigation.tabs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.gitly.presentation.ui.screens.repo_detail.RepoDetailScreen

@Composable
fun RepoDetailsTabScreen(navController: NavHostController) {
    // Use the existing RepoDetailScreen which has search functionality
    RepoDetailScreen(navController = navController)
}
