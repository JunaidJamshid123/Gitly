package com.example.gitly.domain.usecase.favorites

import com.example.gitly.domain.model.Repository
import com.example.gitly.domain.repository.FavoritesRepository
import javax.inject.Inject

/**
 * Use case for toggling repository favorite status.
 */
class ToggleFavoriteRepositoryUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    /**
     * Toggle the favorite status of a repository.
     * @param repository The repository to toggle
     * @return true if the repository is now a favorite, false otherwise
     */
    suspend operator fun invoke(repository: Repository): Boolean {
        return favoritesRepository.toggleFavoriteRepository(repository)
    }
}
