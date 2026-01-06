package com.example.gitly.data.local.dao

import androidx.room.*
import com.example.gitly.data.local.entity.FavoriteRepositoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Favorite Repositories.
 */
@Dao
interface FavoriteRepositoryDao {
    
    @Query("SELECT * FROM favorite_repositories ORDER BY savedAt DESC")
    fun getAllFavoriteRepositories(): Flow<List<FavoriteRepositoryEntity>>
    
    @Query("SELECT * FROM favorite_repositories WHERE id = :repositoryId")
    suspend fun getFavoriteRepositoryById(repositoryId: Long): FavoriteRepositoryEntity?
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_repositories WHERE id = :repositoryId)")
    suspend fun isFavorite(repositoryId: Long): Boolean
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRepository(repository: FavoriteRepositoryEntity)
    
    @Delete
    suspend fun deleteFavoriteRepository(repository: FavoriteRepositoryEntity)
    
    @Query("DELETE FROM favorite_repositories WHERE id = :repositoryId")
    suspend fun deleteFavoriteRepositoryById(repositoryId: Long)
    
    @Query("DELETE FROM favorite_repositories")
    suspend fun deleteAllFavoriteRepositories()
    
    @Query("SELECT COUNT(*) FROM favorite_repositories")
    fun getFavoriteRepositoriesCount(): Flow<Int>
}
