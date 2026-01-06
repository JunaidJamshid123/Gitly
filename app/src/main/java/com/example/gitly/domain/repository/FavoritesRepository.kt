package com.example.gitly.domain.repository

import com.example.gitly.domain.model.Repository
import com.example.gitly.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing favorite repositories and users.
 * This interface defines the contract for local favorites operations.
 */
interface FavoritesRepository {
    
    // ==================== Repository Favorites ====================
    
    /**
     * Get all favorite repositories as a Flow for real-time updates.
     * @return Flow emitting list of favorite repositories
     */
    fun getAllFavoriteRepositories(): Flow<List<Repository>>
    
    /**
     * Check if a repository is marked as favorite.
     * @param repositoryId The repository ID
     * @return true if the repository is a favorite
     */
    suspend fun isRepositoryFavorite(repositoryId: Long): Boolean
    
    /**
     * Add a repository to favorites.
     * @param repository The repository to add
     */
    suspend fun addFavoriteRepository(repository: Repository)
    
    /**
     * Remove a repository from favorites.
     * @param repositoryId The repository ID to remove
     */
    suspend fun removeFavoriteRepository(repositoryId: Long)
    
    /**
     * Toggle the favorite status of a repository.
     * @param repository The repository to toggle
     * @return true if the repository is now a favorite, false otherwise
     */
    suspend fun toggleFavoriteRepository(repository: Repository): Boolean
    
    // ==================== User Favorites ====================
    
    /**
     * Get all favorite users as a Flow for real-time updates.
     * @return Flow emitting list of favorite users
     */
    fun getAllFavoriteUsers(): Flow<List<User>>
    
    /**
     * Check if a user is marked as favorite.
     * @param userId The user ID
     * @return true if the user is a favorite
     */
    suspend fun isUserFavorite(userId: Int): Boolean
    
    /**
     * Add a user to favorites.
     * @param user The user to add
     */
    suspend fun addFavoriteUser(user: User)
    
    /**
     * Remove a user from favorites.
     * @param userId The user ID to remove
     */
    suspend fun removeFavoriteUser(userId: Int)
    
    /**
     * Toggle the favorite status of a user.
     * @param user The user to toggle
     * @return true if the user is now a favorite, false otherwise
     */
    suspend fun toggleFavoriteUser(user: User): Boolean
    
    // ==================== Utility ====================
    
    /**
     * Clear all favorites (both repositories and users).
     */
    suspend fun clearAllFavorites()
    
    /**
     * Get the total count of all favorites.
     * @return Total count of favorite repositories and users
     */
    fun getTotalFavoritesCount(): Flow<Int>
}
