package com.example.gitly.presentation.ui.screens.user_detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gitly.data.local.AppDatabase
import com.example.gitly.data.local.repository.FavoritesRepository
import com.example.gitly.data.model.ContributionCalendar
import com.example.gitly.data.model.GitHubRepo
import com.example.gitly.data.model.GitHubUser
import com.example.gitly.data.repository.GeminiRepository
import com.example.gitly.data.repository.GitHubRepository
import com.example.gitly.data.repository.LanguageStat
import com.example.gitly.data.repository.RepoSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserSearchState(
    val isLoading: Boolean = false,
    val allUsers: List<GitHubUser> = emptyList(),
    val displayedUsers: List<GitHubUser> = emptyList(),
    val error: String? = null,
    val favoriteUserIds: Set<Int> = emptySet(),
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val pageSize: Int = 10
)

data class UserReposState(
    val isLoading: Boolean = false,
    val repos: List<GitHubRepo> = emptyList(),
    val error: String? = null
)

data class ContributionState(
    val isLoading: Boolean = false,
    val contributionCalendar: ContributionCalendar? = null,
    val error: String? = null
)

data class AiSummaryState(
    val isLoading: Boolean = false,
    val summary: String? = null,
    val error: String? = null,
    val showDialog: Boolean = false
)

class UserDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GitHubRepository()
    private val database = AppDatabase.getDatabase(application)
    private val favoritesRepository = FavoritesRepository(
        database.favoriteRepoDao(),
        database.favoriteUserDao()
    )
    
    // GeminiRepository will be set via setter injection from the screen
    private var geminiRepository: GeminiRepository? = null
    
    fun setGeminiRepository(repo: GeminiRepository) {
        geminiRepository = repo
    }
    
    private val _searchState = MutableStateFlow(UserSearchState())
    val searchState: StateFlow<UserSearchState> = _searchState.asStateFlow()
    
    private val _userDetailState = MutableStateFlow<GitHubUser?>(null)
    val userDetailState: StateFlow<GitHubUser?> = _userDetailState.asStateFlow()
    
    private val _userReposState = MutableStateFlow(UserReposState())
    val userReposState: StateFlow<UserReposState> = _userReposState.asStateFlow()
    
    private val _contributionState = MutableStateFlow(ContributionState())
    val contributionState: StateFlow<ContributionState> = _contributionState.asStateFlow()
    
    private val _aiSummaryState = MutableStateFlow(AiSummaryState())
    val aiSummaryState: StateFlow<AiSummaryState> = _aiSummaryState.asStateFlow()
    
    // Cache to prevent duplicate searches
    private val searchCache = mutableMapOf<String, List<GitHubUser>>()
    private var lastSearchQuery = ""
    
    init {
        loadFavoriteUserIds()
    }
    
    private fun loadFavoriteUserIds() {
        viewModelScope.launch {
            favoritesRepository.getAllFavoriteUsers().collect { favoriteUsers ->
                _searchState.value = _searchState.value.copy(
                    favoriteUserIds = favoriteUsers.map { it.id }.toSet()
                )
            }
        }
    }
    
    fun toggleFavorite(user: GitHubUser) {
        viewModelScope.launch {
            favoritesRepository.toggleFavoriteUser(user)
        }
    }
    
    fun nextPage() {
        val state = _searchState.value
        if (state.currentPage < state.totalPages) {
            updateDisplayedUsers(state.currentPage + 1)
        }
    }
    
    fun previousPage() {
        val state = _searchState.value
        if (state.currentPage > 1) {
            updateDisplayedUsers(state.currentPage - 1)
        }
    }
    
    private fun updateDisplayedUsers(page: Int) {
        val state = _searchState.value
        val startIndex = (page - 1) * state.pageSize
        val endIndex = minOf(startIndex + state.pageSize, state.allUsers.size)
        val displayedUsers = state.allUsers.subList(startIndex, endIndex)
        
        _searchState.value = state.copy(
            currentPage = page,
            displayedUsers = displayedUsers
        )
    }
    
    fun searchUsers(query: String) {
        if (query.isBlank() || query.length < 3) {
            _searchState.value = UserSearchState()
            return
        }
        
        // Skip if same as last query to avoid duplicate calls
        if (query == lastSearchQuery) {
            return
        }
        lastSearchQuery = query
        
        // Check cache first
        searchCache[query]?.let { cachedUsers ->
            val totalPages = if (cachedUsers.isEmpty()) 1 else (cachedUsers.size + _searchState.value.pageSize - 1) / _searchState.value.pageSize
            val displayedUsers = cachedUsers.take(_searchState.value.pageSize)
            _searchState.value = _searchState.value.copy(
                isLoading = false,
                allUsers = cachedUsers,
                displayedUsers = displayedUsers,
                currentPage = 1,
                totalPages = totalPages,
                error = null
            )
            return
        }
        
        viewModelScope.launch {
            _searchState.value = _searchState.value.copy(isLoading = true)
            
            repository.searchUsers(query).fold(
                onSuccess = { basicUsers ->
                    // Fetch detailed info for only 20 users to reduce API calls
                    val detailedUsers = basicUsers.take(20).mapNotNull { user ->
                        try {
                            repository.getUserDetails(user.login).getOrNull()
                        } catch (e: Exception) {
                            // If fetch fails, keep the basic user info
                            user
                        }
                    }
                    
                    // Cache the results
                    searchCache[query] = detailedUsers
                    
                    val totalPages = if (detailedUsers.isEmpty()) 1 else (detailedUsers.size + _searchState.value.pageSize - 1) / _searchState.value.pageSize
                    val displayedUsers = detailedUsers.take(_searchState.value.pageSize)
                    
                    _searchState.value = _searchState.value.copy(
                        isLoading = false,
                        allUsers = detailedUsers,
                        displayedUsers = displayedUsers,
                        currentPage = 1,
                        totalPages = totalPages
                    )
                },
                onFailure = { exception ->
                    val errorMessage = when {
                        exception.message?.contains("429") == true -> 
                            "Too many requests! Please wait a moment and try again."
                        exception.message?.contains("403") == true -> 
                            "Rate limit exceeded. Please try again later."
                        else -> exception.message ?: "An error occurred"
                    }
                    _searchState.value = _searchState.value.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
            )
        }
    }
    
    fun getUserDetails(username: String) {
        viewModelScope.launch {
            repository.getUserDetails(username).fold(
                onSuccess = { user ->
                    _userDetailState.value = user
                    // Fetch repositories and contributions when user details are loaded
                    getUserRepositories(username)
                    getUserContributions(username)
                },
                onFailure = { exception ->
                    // Handle error
                }
            )
        }
    }
    
    fun getUserRepositories(username: String) {
        viewModelScope.launch {
            _userReposState.value = UserReposState(isLoading = true)
            
            repository.getUserRepositories(username).fold(
                onSuccess = { repos ->
                    _userReposState.value = UserReposState(
                        isLoading = false,
                        repos = repos
                    )
                },
                onFailure = { exception ->
                    _userReposState.value = UserReposState(
                        isLoading = false,
                        error = exception.message ?: "Failed to load repositories"
                    )
                }
            )
        }
    }
    
    fun getUserContributions(username: String) {
        viewModelScope.launch {
            _contributionState.value = ContributionState(isLoading = true)
            
            repository.getUserContributions(username).fold(
                onSuccess = { calendar ->
                    _contributionState.value = ContributionState(
                        isLoading = false,
                        contributionCalendar = calendar
                    )
                },
                onFailure = { exception ->
                    _contributionState.value = ContributionState(
                        isLoading = false,
                        error = exception.message ?: "Failed to load contributions"
                    )
                }
            )
        }
    }
    
    fun clearUserDetails() {
        _userDetailState.value = null
        _userReposState.value = UserReposState()
    }
    
    /**
     * Generate AI summary for the current user profile
     */
    fun generateAiSummary() {
        val gemini = geminiRepository ?: run {
            _aiSummaryState.value = AiSummaryState(
                error = "AI service not available",
                showDialog = true
            )
            return
        }
        
        val user = _userDetailState.value ?: run {
            _aiSummaryState.value = AiSummaryState(
                error = "User data not loaded",
                showDialog = true
            )
            return
        }
        
        val repos = _userReposState.value.repos
        val contributions = _contributionState.value.contributionCalendar
        
        viewModelScope.launch {
            _aiSummaryState.value = AiSummaryState(isLoading = true, showDialog = true)
            
            // Calculate language statistics from repos
            val languageStats = calculateLanguageStats(repos)
            
            // Convert repos to RepoSummary
            val repoSummaries = repos.map { repo ->
                RepoSummary(
                    name = repo.name,
                    description = repo.description,
                    stars = repo.stargazersCount ?: 0,
                    forks = repo.forksCount ?: 0,
                    language = repo.language
                )
            }
            
            gemini.generateUserSummary(
                username = user.login,
                name = user.name,
                bio = user.bio,
                location = user.location,
                company = user.company,
                blog = user.blog,
                twitterUsername = user.twitter_username,
                publicRepos = user.public_repos,
                publicGists = user.public_gists,
                followers = user.followers,
                following = user.following,
                createdAt = user.created_at,
                repositories = repoSummaries,
                totalContributions = contributions?.totalContributions,
                languageStats = languageStats
            ).fold(
                onSuccess = { summary ->
                    _aiSummaryState.value = AiSummaryState(
                        summary = summary,
                        showDialog = true
                    )
                },
                onFailure = { exception ->
                    _aiSummaryState.value = AiSummaryState(
                        error = exception.message ?: "Failed to generate summary",
                        showDialog = true
                    )
                }
            )
        }
    }
    
    private fun calculateLanguageStats(repos: List<GitHubRepo>): Map<String, LanguageStat> {
        val languageCounts = mutableMapOf<String, Int>()
        
        repos.forEach { repo ->
            repo.language?.let { lang ->
                languageCounts[lang] = (languageCounts[lang] ?: 0) + 1
            }
        }
        
        val total = languageCounts.values.sum().toFloat()
        
        return languageCounts.mapValues { (_, count) ->
            LanguageStat(
                repoCount = count,
                percentage = if (total > 0) (count / total * 100) else 0f
            )
        }
    }
    
    fun dismissAiSummaryDialog() {
        _aiSummaryState.value = AiSummaryState()
    }
}
