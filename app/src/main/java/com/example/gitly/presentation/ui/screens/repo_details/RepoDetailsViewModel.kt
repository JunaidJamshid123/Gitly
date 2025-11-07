package com.example.gitly.presentation.ui.screens.repo_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gitly.data.model.GitHubRepo
import com.example.gitly.data.repository.GitHubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RepoDetailsState(
    val repository: GitHubRepo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class RepoDetailsViewModel : ViewModel() {
    private val repository = GitHubRepository()
    
    private val _state = MutableStateFlow(RepoDetailsState())
    val state: StateFlow<RepoDetailsState> = _state.asStateFlow()
    
    fun loadRepositoryDetails(owner: String, repo: String) {
        viewModelScope.launch {
            _state.value = RepoDetailsState(isLoading = true)
            
            repository.getRepositoryDetails(owner, repo).fold(
                onSuccess = { repoDetails ->
                    _state.value = RepoDetailsState(
                        repository = repoDetails,
                        isLoading = false,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _state.value = RepoDetailsState(
                        repository = null,
                        isLoading = false,
                        error = exception.message ?: "Failed to load repository details"
                    )
                }
            )
        }
    }
}
