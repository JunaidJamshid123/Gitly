package com.example.gitly.domain.usecase.favorites

import com.example.gitly.domain.repository.FavoritesRepository
import javax.inject.Inject

/**
 * Use case for checking if a user is favorite.
 */
class IsUserFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(userId: Int): Boolean {
        return favoritesRepository.isUserFavorite(userId)
    }
}
