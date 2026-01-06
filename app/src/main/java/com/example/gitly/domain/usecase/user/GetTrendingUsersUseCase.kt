package com.example.gitly.domain.usecase.user

import com.example.gitly.domain.model.User
import com.example.gitly.domain.repository.GitHubRepository
import com.example.gitly.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting trending users on GitHub.
 */
class GetTrendingUsersUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    /**
     * Execute the use case.
     * @return Flow emitting Resource with list of trending users
     */
    operator fun invoke(): Flow<Resource<List<User>>> {
        return repository.getTrendingUsers()
    }
}
