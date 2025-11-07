package com.example.gitly.presentation.navigation
import androidx.annotation.DrawableRes
import com.example.gitly.R
sealed class BottomNavItem (
    val route: String,
    val title: String,
    @DrawableRes val icon: Int
)
{
    object Home : BottomNavItem("home", "Home", R.drawable.homee)
    object RepoDetails : BottomNavItem("repo_details", "Repositories", R.drawable.code)
    object UserDetails : BottomNavItem("user_details", "Users", R.drawable.user)
    object AiInsights : BottomNavItem("ai_insights", "AI Insights", R.drawable.ai_insight)
    object Favorites : BottomNavItem("favorites", "Favorites", R.drawable.fav)
}