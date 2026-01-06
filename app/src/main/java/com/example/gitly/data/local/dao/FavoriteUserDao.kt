package com.example.gitly.data.local.dao

import androidx.room.*
import com.example.gitly.data.local.entity.FavoriteUserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Favorite Users.
 */
@Dao
interface FavoriteUserDao {
    
    @Query("SELECT * FROM favorite_users ORDER BY savedAt DESC")
    fun getAllFavoriteUsers(): Flow<List<FavoriteUserEntity>>
    
    @Query("SELECT * FROM favorite_users WHERE id = :userId")
    suspend fun getFavoriteUserById(userId: Int): FavoriteUserEntity?
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_users WHERE id = :userId)")
    suspend fun isFavorite(userId: Int): Boolean
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteUser(user: FavoriteUserEntity)
    
    @Delete
    suspend fun deleteFavoriteUser(user: FavoriteUserEntity)
    
    @Query("DELETE FROM favorite_users WHERE id = :userId")
    suspend fun deleteFavoriteUserById(userId: Int)
    
    @Query("DELETE FROM favorite_users")
    suspend fun deleteAllFavoriteUsers()
    
    @Query("SELECT COUNT(*) FROM favorite_users")
    fun getFavoriteUsersCount(): Flow<Int>
}
