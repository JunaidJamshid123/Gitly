package com.example.gitly.presentation.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gitly.data.model.GitHubRepo
import com.example.gitly.data.model.GitHubUser
import com.example.gitly.data.repository.GitHubRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class HomeScreenState(
    val isLoading: Boolean = false,
    val trendingRepos: List<GitHubRepo> = emptyList(),
    val trendingUsers: List<GitHubUser> = emptyList(),
    val error: String? = null,
    val githubFact: String = "",
    val userName: String = "Explorer",
    val topLanguages: List<LanguageStat> = emptyList(),
    val isLoadingLanguages: Boolean = false
)

data class LanguageStat(
    val name: String,
    val percentage: Float,
    val growth: String = "",
    val repoCount: Int = 0
)

class HomeScreenViewModel : ViewModel() {
    private val repository = GitHubRepository()
    
    private val _homeState = MutableStateFlow(HomeScreenState())
    val homeState: StateFlow<HomeScreenState> = _homeState.asStateFlow()
    
    init {
        loadHomeData()
    }
    
    fun loadHomeData() {
        viewModelScope.launch {
            _homeState.value = _homeState.value.copy(
                isLoading = true,
                isLoadingLanguages = true,
                githubFact = getRandomGitHubFact()
            )
            
            try {
                // Fetch trending repos, users, and language stats in parallel
                val trendingReposDeferred = async { fetchTrendingRepositories() }
                val trendingUsersDeferred = async { fetchTrendingUsers() }
                val languageStatsDeferred = async { fetchRealLanguageStats() }
                
                val repos = trendingReposDeferred.await()
                val users = trendingUsersDeferred.await()
                val languages = languageStatsDeferred.await()
                
                _homeState.value = _homeState.value.copy(
                    isLoading = false,
                    isLoadingLanguages = false,
                    trendingRepos = repos,
                    trendingUsers = users,
                    topLanguages = languages,
                    error = null
                )
            } catch (e: Exception) {
                _homeState.value = _homeState.value.copy(
                    isLoading = false,
                    isLoadingLanguages = false,
                    error = e.message ?: "Failed to load data",
                    topLanguages = getTopLanguages() // Fallback to default data
                )
            }
        }
    }
    
    private suspend fun fetchTrendingRepositories(): List<GitHubRepo> {
        // Get repositories created in the last 7 days, sorted by stars
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val weekAgo = dateFormat.format(calendar.time)
        
        val query = "created:>$weekAgo stars:>100"
        
        return repository.searchRepositories(query).getOrElse { emptyList() }
    }
    
    private suspend fun fetchTrendingUsers(): List<GitHubUser> {
        // Get users with high follower count
        val query = "followers:>10000"
        
        return repository.searchUsers(query).getOrElse { emptyList() }
    }
    
    private fun getRandomGitHubFact(): String {
        val facts = listOf(
            "Over 100 million developers use GitHub worldwide! 🌍",
            "GitHub hosts over 420 million repositories! 📦",
            "The most starred repository on GitHub has over 400K stars! ⭐",
            "GitHub processes over 2 billion API requests daily! 🚀",
            "Microsoft acquired GitHub for $7.5 billion in 2018! 💰",
            "GitHub Actions processes millions of workflows every day! ⚡",
            "The average developer pushes 3-4 commits per day! 💻",
            "GitHub Pages hosts over 100 million websites for free! 🌐",
            "There are over 330 programming languages on GitHub! 🎨",
            "GitHub Copilot writes 40% of code in supported IDEs! 🤖",
            "The Linux kernel repository has over 1 million commits! 🐧",
            "GitHub's mascot Octocat has over 1000 variations! 🐙",
            "Fortune 500 companies have over 140K repositories on GitHub! 🏢",
            "GitHub Discussions host millions of conversations! 💬",
            "Over 90% of Fortune 100 companies use GitHub! 📊"
        )
        return facts.random()
    }
    
    fun refreshData() {
        loadHomeData()
    }
    
    private suspend fun fetchRealLanguageStats(): List<LanguageStat> {
        return try {
            val languages = listOf(
                "JavaScript", "Python", "Java", "Kotlin", 
                "TypeScript", "Go", "Rust", "Swift", "C++"
            )
            
            val languageStats = mutableListOf<LanguageStat>()
            var totalRepos = 0
            
            // Fetch repo counts for each language
            languages.forEach { language ->
                try {
                    val query = "language:${language.lowercase()}"
                    val response = repository.searchRepositoriesWithCount(query, 1) // Only need total_count
                    response.fold(
                        onSuccess = { searchResponse ->
                            val count = searchResponse.totalCount.coerceAtMost(10000000) // Cap at 10M for reasonable numbers
                            totalRepos += count
                            languageStats.add(LanguageStat(language, 0f, "", count))
                        },
                        onFailure = { e ->
                            println("Failed to fetch stats for $language: ${e.message}")
                        }
                    )
                } catch (e: Exception) {
                    // If API fails for a language, skip it
                    println("Failed to fetch stats for $language: ${e.message}")
                }
            }
            
            // Calculate percentages based on actual counts
            if (totalRepos > 0) {
                languageStats.map { stat ->
                    val percentage = (stat.repoCount.toFloat() / totalRepos.toFloat()) * 100f
                    stat.copy(percentage = percentage)
                }.sortedByDescending { it.repoCount }
            } else {
                getTopLanguages() // Fallback to default data
            }
        } catch (e: Exception) {
            println("Failed to fetch language stats: ${e.message}")
            getTopLanguages() // Fallback to default data
        }
    }
    
    private fun getTopLanguages(): List<LanguageStat> {
        // Fallback data based on GitHub's actual statistics
        return listOf(
            LanguageStat("JavaScript", 44.0f, "+2.3%", 11),
            LanguageStat("Python", 20.0f, "+5.1%", 5),
            LanguageStat("Java", 12.0f, "+1.2%", 3),
            LanguageStat("Kotlin", 8.0f, "+7.3%", 2),
            LanguageStat("TypeScript", 8.0f, "+8.7%", 2),
            LanguageStat("Go", 4.0f, "+6.2%", 1),
            LanguageStat("Rust", 2.0f, "+12.4%", 1),
            LanguageStat("Swift", 1.0f, "+4.5%", 1),
            LanguageStat("C++", 1.0f, "+1.8%", 1)
        )
    }
}