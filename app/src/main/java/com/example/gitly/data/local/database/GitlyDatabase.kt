package com.example.gitly.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gitly.data.local.dao.FavoriteRepositoryDao
import com.example.gitly.data.local.dao.FavoriteUserDao
import com.example.gitly.data.local.entity.FavoriteRepositoryEntity
import com.example.gitly.data.local.entity.FavoriteUserEntity

/**
 * Room Database for Gitly app.
 * Contains tables for favorite repositories and users.
 */
@Database(
    entities = [
        FavoriteRepositoryEntity::class,
        FavoriteUserEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class GitlyDatabase : RoomDatabase() {
    
    abstract fun favoriteRepositoryDao(): FavoriteRepositoryDao
    abstract fun favoriteUserDao(): FavoriteUserDao
    
    companion object {
        const val DATABASE_NAME = "gitly_database"
    }
}
