package com.example.gitly.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gitly.data.api.GitHubApiService
import com.example.gitly.data.model.RepositoryStatistics
import com.example.gitly.data.model.TopRepository
import com.example.gitly.data.model.UserStatistics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StatisticsViewModel(private val apiService: GitHubApiService) : ViewModel() {

    private val _userStatsState = MutableStateFlow<UserStatsState>(UserStatsState.Loading)
    val userStatsState: StateFlow<UserStatsState> = _userStatsState

    private val _repoStatsState = MutableStateFlow<RepoStatsState>(RepoStatsState.Loading)
    val repoStatsState: StateFlow<RepoStatsState> = _repoStatsState

    fun fetchUserStatistics(username: String) {
        viewModelScope.launch {
            _userStatsState.value = UserStatsState.Loading
            try {
                // Fetch user details
                val user = apiService.getUserDetails(username)
                
                // Fetch user repositories
                val repos = apiService.getUserRepositories(username)
                
                // Calculate statistics
                val totalStars = repos.sumOf { it.stargazersCount ?: 0 }
                val totalForks = repos.sumOf { it.forksCount ?: 0 }
                
                // Calculate language statistics
                val languageStats = mutableMapOf<String, Int>()
                repos.forEach { repo ->
                    repo.language?.let { lang ->
                        languageStats[lang] = (languageStats[lang] ?: 0) + 1
                    }
                }
                
                // Get top 3 repositories by stars
                val topRepos = repos
                    .sortedByDescending { it.stargazersCount ?: 0 }
                    .take(3)
                    .map { repo ->
                        TopRepository(
                            name = repo.name,
                            stars = repo.stargazersCount ?: 0,
                            forks = repo.forksCount ?: 0,
                            language = repo.language
                        )
                    }
                
                // Calculate account age
                val accountAge = calculateAccountAge(user.created_at ?: "")
                
                val statistics = UserStatistics(
                    username = user.login,
                    avatarUrl = user.avatar_url,
                    name = user.name,
                    totalRepos = user.public_repos,
                    totalStars = totalStars,
                    totalForks = totalForks,
                    followers = user.followers,
                    following = user.following,
                    accountAge = accountAge,
                    languageStats = languageStats,
                    topRepositories = topRepos
                )
                
                _userStatsState.value = UserStatsState.Success(statistics)
            } catch (e: Exception) {
                _userStatsState.value = UserStatsState.Error(e.message ?: "Failed to fetch statistics")
            }
        }
    }

    fun fetchRepositoryStatistics(owner: String, repoName: String) {
        viewModelScope.launch {
            _repoStatsState.value = RepoStatsState.Loading
            try {
                // Fetch repository details
                val repo = apiService.getRepository(owner, repoName)
                
                // For language stats, we'll use a simple distribution
                // In a real app, you'd call the languages API endpoint
                val languageStats = mutableMapOf<String, Int>()
                repo.language?.let {
                    languageStats[it] = 100 // Main language at 100%
                }
                
                val statistics = RepositoryStatistics(
                    name = repo.name,
                    fullName = repo.fullName,
                    description = repo.description,
                    stars = repo.stargazersCount ?: 0,
                    forks = repo.forksCount ?: 0,
                    watchers = repo.watchersCount ?: 0,
                    openIssues = repo.openIssuesCount ?: 0,
                    size = 0, // Size not available in current model
                    language = repo.language,
                    languageStats = languageStats,
                    createdAt = formatDate(repo.createdAt ?: ""),
                    updatedAt = formatDate(repo.updatedAt ?: "")
                )
                
                _repoStatsState.value = RepoStatsState.Success(statistics)
            } catch (e: Exception) {
                _repoStatsState.value = RepoStatsState.Error(e.message ?: "Failed to fetch statistics")
            }
        }
    }

    private fun calculateAccountAge(createdAt: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val date = sdf.parse(createdAt)
            val now = Date()
            val diffInMillis = now.time - (date?.time ?: 0)
            val years = diffInMillis / (1000L * 60 * 60 * 24 * 365)
            val months = (diffInMillis % (1000L * 60 * 60 * 24 * 365)) / (1000L * 60 * 60 * 24 * 30)
            
            when {
                years > 0 -> "$years year${if (years > 1) "s" else ""}"
                months > 0 -> "$months month${if (months > 1) "s" else ""}"
                else -> "Less than a month"
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val date = sdf.parse(dateString)
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }
}

sealed class UserStatsState {
    object Loading : UserStatsState()
    data class Success(val statistics: UserStatistics) : UserStatsState()
    data class Error(val message: String) : UserStatsState()
}

sealed class RepoStatsState {
    object Loading : RepoStatsState()
    data class Success(val statistics: RepositoryStatistics) : RepoStatsState()
    data class Error(val message: String) : RepoStatsState()
}
