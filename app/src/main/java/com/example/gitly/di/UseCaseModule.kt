package com.example.gitly.di

import com.example.gitly.domain.repository.FavoritesRepository
import com.example.gitly.domain.repository.GitHubRepository
import com.example.gitly.domain.usecase.favorites.*
import com.example.gitly.domain.usecase.repository.*
import com.example.gitly.domain.usecase.user.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing UseCase dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    // ==================== User UseCases ====================
    
    @Provides
    @Singleton
    fun provideSearchUsersUseCase(
        repository: GitHubRepository
    ): SearchUsersUseCase {
        return SearchUsersUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetUserDetailsUseCase(
        repository: GitHubRepository
    ): GetUserDetailsUseCase {
        return GetUserDetailsUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetTrendingUsersUseCase(
        repository: GitHubRepository
    ): GetTrendingUsersUseCase {
        return GetTrendingUsersUseCase(repository)
    }
    
    // ==================== Repository UseCases ====================
    
    @Provides
    @Singleton
    fun provideSearchRepositoriesUseCase(
        repository: GitHubRepository
    ): SearchRepositoriesUseCase {
        return SearchRepositoriesUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetRepositoryDetailsUseCase(
        repository: GitHubRepository
    ): GetRepositoryDetailsUseCase {
        return GetRepositoryDetailsUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetUserRepositoriesUseCase(
        repository: GitHubRepository
    ): GetUserRepositoriesUseCase {
        return GetUserRepositoriesUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetTrendingRepositoriesUseCase(
        repository: GitHubRepository
    ): GetTrendingRepositoriesUseCase {
        return GetTrendingRepositoriesUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetLanguageStatsUseCase(
        repository: GitHubRepository
    ): GetLanguageStatsUseCase {
        return GetLanguageStatsUseCase(repository)
    }
    
    // ==================== Favorites UseCases ====================
    
    @Provides
    @Singleton
    fun provideGetFavoriteRepositoriesUseCase(
        favoritesRepository: FavoritesRepository
    ): GetFavoriteRepositoriesUseCase {
        return GetFavoriteRepositoriesUseCase(favoritesRepository)
    }
    
    @Provides
    @Singleton
    fun provideGetFavoriteUsersUseCase(
        favoritesRepository: FavoritesRepository
    ): GetFavoriteUsersUseCase {
        return GetFavoriteUsersUseCase(favoritesRepository)
    }
    
    @Provides
    @Singleton
    fun provideToggleFavoriteRepositoryUseCase(
        favoritesRepository: FavoritesRepository
    ): ToggleFavoriteRepositoryUseCase {
        return ToggleFavoriteRepositoryUseCase(favoritesRepository)
    }
    
    @Provides
    @Singleton
    fun provideToggleFavoriteUserUseCase(
        favoritesRepository: FavoritesRepository
    ): ToggleFavoriteUserUseCase {
        return ToggleFavoriteUserUseCase(favoritesRepository)
    }
    
    @Provides
    @Singleton
    fun provideIsRepositoryFavoriteUseCase(
        favoritesRepository: FavoritesRepository
    ): IsRepositoryFavoriteUseCase {
        return IsRepositoryFavoriteUseCase(favoritesRepository)
    }
    
    @Provides
    @Singleton
    fun provideIsUserFavoriteUseCase(
        favoritesRepository: FavoritesRepository
    ): IsUserFavoriteUseCase {
        return IsUserFavoriteUseCase(favoritesRepository)
    }
}
