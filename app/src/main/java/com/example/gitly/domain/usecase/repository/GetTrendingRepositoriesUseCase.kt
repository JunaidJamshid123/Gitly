package com.example.gitly.domain.usecase.repository

import com.example.gitly.domain.model.Repository
import com.example.gitly.domain.repository.GitHubRepository
import com.example.gitly.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting trending repositories.
 */
class GetTrendingRepositoriesUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    /**
     * Execute the use case.
     * @return Flow emitting Resource with list of trending repositories
     */
    operator fun invoke(): Flow<Resource<List<Repository>>> {
        return repository.getTrendingRepositories()
    }
}
