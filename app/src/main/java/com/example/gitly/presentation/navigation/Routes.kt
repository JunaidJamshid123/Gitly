package com.example.gitly.presentation.navigation

/**
 * Navigation routes for the app.
 */
object Routes {
    const val SPLASH = "splash"
    const val MAIN = "main"
    const val HOME = "home"
    const val SEARCH = "search"
    const val SEARCH_WITH_QUERY = "search?query={query}"
    const val FAVORITES = "favorites"
    const val USER_DETAIL = "user/{username}"
    const val REPO_DETAIL = "repo/{owner}/{repo}"
    
    fun userDetail(username: String) = "user/$username"
    fun repoDetail(owner: String, repo: String) = "repo/$owner/$repo"
    fun searchWithQuery(query: String) = "search?query=$query"
}
