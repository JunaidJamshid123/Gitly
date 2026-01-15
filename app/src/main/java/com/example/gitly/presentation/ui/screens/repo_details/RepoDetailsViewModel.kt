package com.example.gitly.presentation.ui.screens.repo_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gitly.data.model.GitHubRepo
import com.example.gitly.data.repository.GeminiRepository
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

data class RepoAiSummaryState(
    val isLoading: Boolean = false,
    val summary: String? = null,
    val error: String? = null,
    val showDialog: Boolean = false
)

class RepoDetailsViewModel : ViewModel() {
    private val repository = GitHubRepository()
    
    // GeminiRepository will be set via setter injection from the screen
    private var geminiRepository: GeminiRepository? = null
    
    fun setGeminiRepository(repo: GeminiRepository) {
        geminiRepository = repo
    }
    
    private val _state = MutableStateFlow(RepoDetailsState())
    val state: StateFlow<RepoDetailsState> = _state.asStateFlow()
    
    private val _aiSummaryState = MutableStateFlow(RepoAiSummaryState())
    val aiSummaryState: StateFlow<RepoAiSummaryState> = _aiSummaryState.asStateFlow()
    
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
    
    /**
     * Generate AI summary for the current repository
     */
    fun generateAiSummary() {
        val gemini = geminiRepository ?: run {
            _aiSummaryState.value = RepoAiSummaryState(
                error = "AI service not available",
                showDialog = true
            )
            return
        }
        
        val repo = _state.value.repository ?: run {
            _aiSummaryState.value = RepoAiSummaryState(
                error = "Repository data not loaded",
                showDialog = true
            )
            return
        }
        
        viewModelScope.launch {
            _aiSummaryState.value = RepoAiSummaryState(isLoading = true, showDialog = true)
            
            gemini.generateRepoSummary(
                fullName = repo.fullName,
                name = repo.name,
                ownerLogin = repo.owner.login,
                ownerType = repo.owner.type,
                description = repo.description,
                language = repo.language,
                stars = repo.stargazersCount ?: 0,
                forks = repo.forksCount ?: 0,
                watchers = repo.watchersCount ?: 0,
                openIssues = repo.openIssuesCount ?: 0,
                topics = repo.topics,
                createdAt = repo.createdAt,
                updatedAt = repo.updatedAt,
                homepage = repo.homepage,
                isArchived = repo.archived ?: false,
                isFork = repo.fork ?: false,
                visibility = repo.visibility
            ).fold(
                onSuccess = { summary ->
                    _aiSummaryState.value = RepoAiSummaryState(
                        summary = summary,
                        showDialog = true
                    )
                },
                onFailure = { exception ->
                    _aiSummaryState.value = RepoAiSummaryState(
                        error = exception.message ?: "Failed to generate summary",
                        showDialog = true
                    )
                }
            )
        }
    }
    
    fun dismissAiSummaryDialog() {
        _aiSummaryState.value = RepoAiSummaryState()
    }
}
