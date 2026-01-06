package com.example.gitly.domain.usecase.favorites

import com.example.gitly.domain.model.User
import com.example.gitly.domain.repository.FavoritesRepository
import javax.inject.Inject

/**
 * Use case for toggling user favorite status.
 */
class ToggleFavoriteUserUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    /**
     * Toggle the favorite status of a user.
     * @param user The user to toggle
     * @return true if the user is now a favorite, false otherwise
     */
    suspend operator fun invoke(user: User): Boolean {
        return favoritesRepository.toggleFavoriteUser(user)
    }
}
