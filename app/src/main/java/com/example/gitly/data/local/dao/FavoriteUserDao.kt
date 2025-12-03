package com.example.gitly.data.local.dao

import androidx.room.*
import com.example.gitly.data.local.entity.FavoriteUser
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteUserDao {
    
    @Query("SELECT * FROM favorite_users ORDER BY savedAt DESC")
    fun getAllFavoriteUsers(): Flow<List<FavoriteUser>>
    
    @Query("SELECT * FROM favorite_users WHERE id = :userId")
    suspend fun getFavoriteUserById(userId: Int): FavoriteUser?
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_users WHERE id = :userId)")
    suspend fun isFavorite(userId: Int): Boolean
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteUser(user: FavoriteUser)
    
    @Delete
    suspend fun deleteFavoriteUser(user: FavoriteUser)
    
    @Query("DELETE FROM favorite_users WHERE id = :userId")
    suspend fun deleteFavoriteUserById(userId: Int)
    
    @Query("DELETE FROM favorite_users")
    suspend fun deleteAllFavoriteUsers()
}
