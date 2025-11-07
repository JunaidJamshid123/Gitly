package com.example.gitly.presentation.navigation

object NavRoutes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val REPO_DETAILS = "repo_details/{owner}/{repo}"
    
    fun repoDetails(owner: String, repo: String): String {
        return "repo_details/$owner/$repo"
    }
}
