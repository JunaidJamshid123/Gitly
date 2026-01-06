package com.example.gitly.domain.usecase.repository

import com.example.gitly.domain.model.Repository
import com.example.gitly.domain.repository.GitHubRepository
import com.example.gitly.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for searching GitHub repositories.
 */
class SearchRepositoriesUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    /**
     * Execute the use case.
     * @param query The search query
     * @return Flow emitting Resource with list of repositories
     */
    operator fun invoke(query: String): Flow<Resource<List<Repository>>> {
        return repository.searchRepositories(query)
    }
}
