package com.example.gitly.domain.usecase.repository

import com.example.gitly.domain.model.Repository
import com.example.gitly.domain.repository.GitHubRepository
import com.example.gitly.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting repository details.
 */
class GetRepositoryDetailsUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    /**
     * Execute the use case.
     * @param owner Repository owner username
     * @param repo Repository name
     * @return Flow emitting Resource with repository details
     */
    operator fun invoke(owner: String, repo: String): Flow<Resource<Repository>> {
        return repository.getRepositoryDetails(owner, repo)
    }
}
