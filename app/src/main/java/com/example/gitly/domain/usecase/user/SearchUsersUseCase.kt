package com.example.gitly.domain.usecase.user

import com.example.gitly.domain.model.User
import com.example.gitly.domain.repository.GitHubRepository
import com.example.gitly.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for searching GitHub users.
 * This encapsulates the business logic for user search.
 */
class SearchUsersUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    /**
     * Execute the use case.
     * @param query The search query
     * @return Flow emitting Resource with list of users
     */
    operator fun invoke(query: String): Flow<Resource<List<User>>> {
        return repository.searchUsers(query)
    }
}
