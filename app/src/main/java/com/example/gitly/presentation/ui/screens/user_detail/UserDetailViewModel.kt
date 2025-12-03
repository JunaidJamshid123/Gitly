package com.example.gitly.presentation.ui.screens.user_detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gitly.data.local.AppDatabase
import com.example.gitly.data.local.repository.FavoritesRepository
import com.example.gitly.data.model.GitHubRepo
import com.example.gitly.data.model.GitHubUser
import com.example.gitly.data.repository.GitHubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

class UserDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GitHubRepository()
    private val database = AppDatabase.getDatabase(application)
    private val favoritesRepository = FavoritesRepository(
        database.favoriteRepoDao(),
        database.favoriteUserDao()
    )
    
    private val _searchState = MutableStateFlow(UserSearchState())
    val searchState: StateFlow<UserSearchState> = _searchState.asStateFlow()
    
    private val _userDetailState = MutableStateFlow<GitHubUser?>(null)
    val userDetailState: StateFlow<GitHubUser?> = _userDetailState.asStateFlow()
    
    private val _userReposState = MutableStateFlow(UserReposState())
    val userReposState: StateFlow<UserReposState> = _userReposState.asStateFlow()
    
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
        
        viewModelScope.launch {
            _searchState.value = _searchState.value.copy(isLoading = true)
            
            repository.searchUsers(query).fold(
                onSuccess = { basicUsers ->
                    // Fetch detailed info for users to get followers, bio, and repo counts
                    val detailedUsers = basicUsers.take(50).mapNotNull { user ->
                        try {
                            repository.getUserDetails(user.login).getOrNull()
                        } catch (e: Exception) {
                            // If fetch fails, keep the basic user info
                            user
                        }
                    }
                    
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
                    // Fetch repositories when user details are loaded
                    getUserRepositories(username)
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
    
    fun clearUserDetails() {
        _userDetailState.value = null
        _userReposState.value = UserReposState()
    }
}
