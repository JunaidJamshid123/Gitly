package com.example.gitly.di

import com.example.gitly.data.local.dao.FavoriteRepositoryDao
import com.example.gitly.data.local.dao.FavoriteUserDao
import com.example.gitly.data.remote.api.GitHubApiService
import com.example.gitly.data.repository.FavoritesRepositoryImpl
import com.example.gitly.data.repository.GitHubRepositoryImpl
import com.example.gitly.domain.repository.FavoritesRepository
import com.example.gitly.domain.repository.GitHubRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing repository dependencies.
 * Binds repository interfaces to their implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideGitHubRepository(
        apiService: GitHubApiService
    ): GitHubRepository {
        return GitHubRepositoryImpl(apiService)
    }
    
    @Provides
    @Singleton
    fun provideFavoritesRepository(
        favoriteRepositoryDao: FavoriteRepositoryDao,
        favoriteUserDao: FavoriteUserDao
    ): FavoritesRepository {
        return FavoritesRepositoryImpl(favoriteRepositoryDao, favoriteUserDao)
    }
}
