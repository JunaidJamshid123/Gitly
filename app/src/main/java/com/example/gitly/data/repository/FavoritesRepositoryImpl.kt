package com.example.gitly.data.repository

import com.example.gitly.data.local.dao.FavoriteRepositoryDao
import com.example.gitly.data.local.dao.FavoriteUserDao
import com.example.gitly.data.local.mapper.toDomain
import com.example.gitly.data.local.mapper.toEntity
import com.example.gitly.domain.model.Repository
import com.example.gitly.domain.model.User
import com.example.gitly.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of FavoritesRepository.
 * This class handles all local database operations for favorites.
 */
@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val favoriteRepositoryDao: FavoriteRepositoryDao,
    private val favoriteUserDao: FavoriteUserDao
) : FavoritesRepository {
    
    // ==================== Repository Favorites ====================
    
    override fun getAllFavoriteRepositories(): Flow<List<Repository>> {
        return favoriteRepositoryDao.getAllFavoriteRepositories()
            .map { entities -> entities.toDomain() }
    }
    
    override suspend fun isRepositoryFavorite(repositoryId: Long): Boolean {
        return favoriteRepositoryDao.isFavorite(repositoryId)
    }
    
    override suspend fun addFavoriteRepository(repository: Repository) {
        favoriteRepositoryDao.insertFavoriteRepository(repository.toEntity())
    }
    
    override suspend fun removeFavoriteRepository(repositoryId: Long) {
        favoriteRepositoryDao.deleteFavoriteRepositoryById(repositoryId)
    }
    
    override suspend fun toggleFavoriteRepository(repository: Repository): Boolean {
        val isFavorite = isRepositoryFavorite(repository.id)
        if (isFavorite) {
            removeFavoriteRepository(repository.id)
        } else {
            addFavoriteRepository(repository)
        }
        return !isFavorite
    }
    
    // ==================== User Favorites ====================
    
    override fun getAllFavoriteUsers(): Flow<List<User>> {
        return favoriteUserDao.getAllFavoriteUsers()
            .map { entities -> entities.toDomain() }
    }
    
    override suspend fun isUserFavorite(userId: Int): Boolean {
        return favoriteUserDao.isFavorite(userId)
    }
    
    override suspend fun addFavoriteUser(user: User) {
        favoriteUserDao.insertFavoriteUser(user.toEntity())
    }
    
    override suspend fun removeFavoriteUser(userId: Int) {
        favoriteUserDao.deleteFavoriteUserById(userId)
    }
    
    override suspend fun toggleFavoriteUser(user: User): Boolean {
        val isFavorite = isUserFavorite(user.id)
        if (isFavorite) {
            removeFavoriteUser(user.id)
        } else {
            addFavoriteUser(user)
        }
        return !isFavorite
    }
    
    // ==================== Utility ====================
    
    override suspend fun clearAllFavorites() {
        favoriteRepositoryDao.deleteAllFavoriteRepositories()
        favoriteUserDao.deleteAllFavoriteUsers()
    }
    
    override fun getTotalFavoritesCount(): Flow<Int> {
        return combine(
            favoriteRepositoryDao.getFavoriteRepositoriesCount(),
            favoriteUserDao.getFavoriteUsersCount()
        ) { repoCount, userCount ->
            repoCount + userCount
        }
    }
}
