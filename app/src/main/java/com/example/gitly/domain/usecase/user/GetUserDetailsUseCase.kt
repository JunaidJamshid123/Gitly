package com.example.gitly.domain.usecase.user

import com.example.gitly.domain.model.User
import com.example.gitly.domain.repository.GitHubRepository
import com.example.gitly.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting detailed user information.
 */
class GetUserDetailsUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    /**
     * Execute the use case.
     * @param username The GitHub username
     * @return Flow emitting Resource with user details
     */
    operator fun invoke(username: String): Flow<Resource<User>> {
        return repository.getUserDetails(username)
    }
}
