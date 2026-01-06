package com.example.gitly.presentation.ui.screens.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gitly.data.local.AppDatabase
import com.example.gitly.data.local.entity.FavoriteRepo
import com.example.gitly.data.local.entity.FavoriteUserEntity
import com.example.gitly.data.local.repository.FavoritesRepository
import com.example.gitly.data.model.GitHubRepo
import com.example.gitly.data.model.GitHubUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FavoritesState(
    val favoriteRepos: List<FavoriteRepo> = emptyList(),
    val favoriteUsers: List<FavoriteUserEntity> = emptyList(),
    val selectedTab: FavoriteTab = FavoriteTab.REPOS,
    val isLoading: Boolean = false
)

enum class FavoriteTab {
    REPOS, USERS
}

class FavoritesViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val favoritesRepository = FavoritesRepository(
        database.favoriteRepoDao(),
        database.favoriteUserDao()
    )
    
    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state.asStateFlow()
    
    init {
        loadFavorites()
    }
    
    private fun loadFavorites() {
        viewModelScope.launch {
            favoritesRepository.getAllFavoriteRepos().collect { repos ->
                _state.value = _state.value.copy(favoriteRepos = repos)
            }
        }
        
        viewModelScope.launch {
            favoritesRepository.getAllFavoriteUsers().collect { users ->
                _state.value = _state.value.copy(favoriteUsers = users)
            }
        }
    }
    
    fun selectTab(tab: FavoriteTab) {
        _state.value = _state.value.copy(selectedTab = tab)
    }
    
    fun toggleRepoFavorite(repo: GitHubRepo) {
        viewModelScope.launch {
            favoritesRepository.toggleFavoriteRepo(repo)
        }
    }
    
    fun toggleUserFavorite(user: GitHubUser) {
        viewModelScope.launch {
            favoritesRepository.toggleFavoriteUser(user)
        }
    }
    
    suspend fun isRepoFavorite(repoId: Long): Boolean {
        return favoritesRepository.isFavoriteRepo(repoId)
    }
    
    suspend fun isUserFavorite(userId: Int): Boolean {
        return favoritesRepository.isFavoriteUser(userId)
    }
    
    fun removeFavoriteRepo(repoId: Long) {
        viewModelScope.launch {
            favoritesRepository.removeFavoriteRepo(repoId)
        }
    }
    
    fun removeFavoriteUser(userId: Int) {
        viewModelScope.launch {
            favoritesRepository.removeFavoriteUser(userId)
        }
    }
}
