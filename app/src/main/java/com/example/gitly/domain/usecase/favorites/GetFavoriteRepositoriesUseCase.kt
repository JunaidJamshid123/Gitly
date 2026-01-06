package com.example.gitly.domain.usecase.favorites

import com.example.gitly.domain.model.Repository
import com.example.gitly.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting all favorite repositories.
 */
class GetFavoriteRepositoriesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    operator fun invoke(): Flow<List<Repository>> {
        return favoritesRepository.getAllFavoriteRepositories()
    }
}
