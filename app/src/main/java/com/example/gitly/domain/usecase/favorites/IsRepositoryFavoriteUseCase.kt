package com.example.gitly.domain.usecase.favorites

import com.example.gitly.domain.repository.FavoritesRepository
import javax.inject.Inject

/**
 * Use case for checking if a repository is favorite.
 */
class IsRepositoryFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(repositoryId: Long): Boolean {
        return favoritesRepository.isRepositoryFavorite(repositoryId)
    }
}
