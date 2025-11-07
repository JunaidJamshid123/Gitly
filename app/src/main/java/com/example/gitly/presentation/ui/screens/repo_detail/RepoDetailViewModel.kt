package com.example.gitly.presentation.ui.screens.repo_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gitly.data.model.GitHubRepo
import com.example.gitly.data.repository.GitHubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RepoSearchState(
    val isLoading: Boolean = false,
    val repos: List<GitHubRepo> = emptyList(),
    val error: String? = null
)

class RepoDetailViewModel : ViewModel() {
    private val repository = GitHubRepository()
    
    private val _searchState = MutableStateFlow(RepoSearchState())
    val searchState: StateFlow<RepoSearchState> = _searchState.asStateFlow()
    
    fun searchRepositories(query: String) {
        if (query.isBlank() || query.length < 3) {
            _searchState.value = RepoSearchState()
            return
        }
        
        viewModelScope.launch {
            _searchState.value = RepoSearchState(isLoading = true)
            
            repository.searchRepositories(query).fold(
                onSuccess = { repos ->
                    _searchState.value = RepoSearchState(
                        isLoading = false,
                        repos = repos
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
                    _searchState.value = RepoSearchState(
                        isLoading = false,
                        error = errorMessage
                    )
                }
            )
        }
    }
}