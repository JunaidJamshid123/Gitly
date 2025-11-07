package com.example.gitly.presentation.ui.screens.user_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gitly.data.model.GitHubRepo
import com.example.gitly.data.model.GitHubUser
import com.example.gitly.data.repository.GitHubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserSearchState(
    val isLoading: Boolean = false,
    val users: List<GitHubUser> = emptyList(),
    val error: String? = null
)

data class UserReposState(
    val isLoading: Boolean = false,
    val repos: List<GitHubRepo> = emptyList(),
    val error: String? = null
)

class UserDetailViewModel : ViewModel() {
    private val repository = GitHubRepository()
    
    private val _searchState = MutableStateFlow(UserSearchState())
    val searchState: StateFlow<UserSearchState> = _searchState.asStateFlow()
    
    private val _userDetailState = MutableStateFlow<GitHubUser?>(null)
    val userDetailState: StateFlow<GitHubUser?> = _userDetailState.asStateFlow()
    
    private val _userReposState = MutableStateFlow(UserReposState())
    val userReposState: StateFlow<UserReposState> = _userReposState.asStateFlow()
    
    fun searchUsers(query: String) {
        if (query.isBlank() || query.length < 3) {
            _searchState.value = UserSearchState()
            return
        }
        
        viewModelScope.launch {
            _searchState.value = UserSearchState(isLoading = true)
            
            repository.searchUsers(query).fold(
                onSuccess = { users ->
                    _searchState.value = UserSearchState(
                        isLoading = false,
                        users = users
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
                    _searchState.value = UserSearchState(
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
