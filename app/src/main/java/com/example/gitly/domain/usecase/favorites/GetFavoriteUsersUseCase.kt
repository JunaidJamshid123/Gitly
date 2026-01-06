package com.example.gitly.domain.usecase.favorites

import com.example.gitly.domain.model.User
import com.example.gitly.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting all favorite users.
 */
class GetFavoriteUsersUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    operator fun invoke(): Flow<List<User>> {
        return favoritesRepository.getAllFavoriteUsers()
    }
}
