package com.example.gitly.data.local.dao

import androidx.room.*
import com.example.gitly.data.local.entity.FavoriteRepo
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteRepoDao {
    
    @Query("SELECT * FROM favorite_repos ORDER BY savedAt DESC")
    fun getAllFavoriteRepos(): Flow<List<FavoriteRepo>>
    
    @Query("SELECT * FROM favorite_repos WHERE id = :repoId")
    suspend fun getFavoriteRepoById(repoId: Long): FavoriteRepo?
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_repos WHERE id = :repoId)")
    suspend fun isFavorite(repoId: Long): Boolean
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRepo(repo: FavoriteRepo)
    
    @Delete
    suspend fun deleteFavoriteRepo(repo: FavoriteRepo)
    
    @Query("DELETE FROM favorite_repos WHERE id = :repoId")
    suspend fun deleteFavoriteRepoById(repoId: Long)
    
    @Query("DELETE FROM favorite_repos")
    suspend fun deleteAllFavoriteRepos()
}
