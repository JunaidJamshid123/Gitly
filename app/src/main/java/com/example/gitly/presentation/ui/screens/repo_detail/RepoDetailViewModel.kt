package com.example.gitly.presentation.ui.screens.repo_detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gitly.data.local.AppDatabase
import com.example.gitly.data.local.repository.FavoritesRepository
import com.example.gitly.data.model.GitHubRepo
import com.example.gitly.data.repository.GitHubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RepoSearchState(
    val isLoading: Boolean = false,
    val allRepos: List<GitHubRepo> = emptyList(),
    val displayedRepos: List<GitHubRepo> = emptyList(),
    val error: String? = null,
    val favoriteRepoIds: Set<Long> = emptySet(),
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val pageSize: Int = 10
)

class RepoDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GitHubRepository()
    private val database = AppDatabase.getDatabase(application)
    private val favoritesRepository = FavoritesRepository(
        database.favoriteRepoDao(),
        database.favoriteUserDao()
    )
    
    private val _searchState = MutableStateFlow(RepoSearchState())
    val searchState: StateFlow<RepoSearchState> = _searchState.asStateFlow()
    
    // Cache to prevent duplicate searches
    private val searchCache = mutableMapOf<String, List<GitHubRepo>>()
    private var lastSearchQuery = ""
    
    init {
        loadFavoriteRepoIds()
    }
    
    private fun loadFavoriteRepoIds() {
        viewModelScope.launch {
            favoritesRepository.getAllFavoriteRepos().collect { favoriteRepos ->
                _searchState.value = _searchState.value.copy(
                    favoriteRepoIds = favoriteRepos.map { it.id }.toSet()
                )
            }
        }
    }
    
    fun toggleFavorite(repo: GitHubRepo) {
        viewModelScope.launch {
            favoritesRepository.toggleFavoriteRepo(repo)
        }
    }
    
    fun nextPage() {
        val state = _searchState.value
        if (state.currentPage < state.totalPages) {
            updateDisplayedRepos(state.currentPage + 1)
        }
    }
    
    fun previousPage() {
        val state = _searchState.value
        if (state.currentPage > 1) {
            updateDisplayedRepos(state.currentPage - 1)
        }
    }
    
    private fun updateDisplayedRepos(page: Int) {
        val state = _searchState.value
        val startIndex = (page - 1) * state.pageSize
        val endIndex = minOf(startIndex + state.pageSize, state.allRepos.size)
        val displayedRepos = state.allRepos.subList(startIndex, endIndex)
        
        _searchState.value = state.copy(
            currentPage = page,
            displayedRepos = displayedRepos
        )
    }
    
    fun searchRepositories(query: String) {
        if (query.isBlank() || query.length < 3) {
            _searchState.value = RepoSearchState()
            return
        }
        
        // Skip if same as last query to avoid duplicate calls
        if (query == lastSearchQuery) {
            return
        }
        lastSearchQuery = query
        
        // Check cache first
        searchCache[query]?.let { cachedRepos ->
            val totalPages = if (cachedRepos.isEmpty()) 1 else (cachedRepos.size + _searchState.value.pageSize - 1) / _searchState.value.pageSize
            val displayedRepos = cachedRepos.take(_searchState.value.pageSize)
            _searchState.value = _searchState.value.copy(
                isLoading = false,
                allRepos = cachedRepos,
                displayedRepos = displayedRepos,
                currentPage = 1,
                totalPages = totalPages,
                error = null
            )
            return
        }
        
        viewModelScope.launch {
            _searchState.value = _searchState.value.copy(isLoading = true)
            
            repository.searchRepositories(query).fold(
                onSuccess = { repos ->
                    // Cache the results
                    searchCache[query] = repos
                    val totalPages = if (repos.isEmpty()) 1 else (repos.size + _searchState.value.pageSize - 1) / _searchState.value.pageSize
                    val displayedRepos = repos.take(_searchState.value.pageSize)
                    
                    _searchState.value = _searchState.value.copy(
                        isLoading = false,
                        allRepos = repos,
                        displayedRepos = displayedRepos,
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
}