package com.example.gitly.di

import android.content.Context
import androidx.room.Room
import com.example.gitly.data.local.dao.FavoriteRepositoryDao
import com.example.gitly.data.local.dao.FavoriteUserDao
import com.example.gitly.data.local.database.GitlyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): GitlyDatabase {
        return Room.databaseBuilder(
            context,
            GitlyDatabase::class.java,
            GitlyDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun provideFavoriteRepositoryDao(database: GitlyDatabase): FavoriteRepositoryDao {
        return database.favoriteRepositoryDao()
    }
    
    @Provides
    @Singleton
    fun provideFavoriteUserDao(database: GitlyDatabase): FavoriteUserDao {
        return database.favoriteUserDao()
    }
}
