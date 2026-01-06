package com.example.gitly.domain.repository

import com.example.gitly.domain.model.Repository
import com.example.gitly.domain.model.SearchResult
import com.example.gitly.domain.model.User
import com.example.gitly.domain.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for GitHub-related operations.
 * This interface defines the contract between the domain and data layers.
 * The data layer will provide the implementation.
 */
interface GitHubRepository {
    
    /**
     * Search for users matching the given query.
     * @param query The search query string
     * @return Flow emitting Resource with list of users
     */
    fun searchUsers(query: String): Flow<Resource<List<User>>>
    
    /**
     * Get detailed information about a specific user.
     * @param username The GitHub username
     * @return Flow emitting Resource with user details
     */
    fun getUserDetails(username: String): Flow<Resource<User>>
    
    /**
     * Search for repositories matching the given query.
     * @param query The search query string
     * @return Flow emitting Resource with list of repositories
     */
    fun searchRepositories(query: String): Flow<Resource<List<Repository>>>
    
    /**
     * Search for repositories with full search result metadata.
     * @param query The search query string
     * @param perPage Number of results per page
     * @return Flow emitting Resource with search result containing total count
     */
    fun searchRepositoriesWithCount(
        query: String,
        perPage: Int = 10
    ): Flow<Resource<SearchResult<Repository>>>
    
    /**
     * Get repositories owned by a specific user.
     * @param username The GitHub username
     * @return Flow emitting Resource with list of repositories
     */
    fun getUserRepositories(username: String): Flow<Resource<List<Repository>>>
    
    /**
     * Get detailed information about a specific repository.
     * @param owner The repository owner's username
     * @param repo The repository name
     * @return Flow emitting Resource with repository details
     */
    fun getRepositoryDetails(owner: String, repo: String): Flow<Resource<Repository>>
    
    /**
     * Get trending repositories from the past week.
     * @return Flow emitting Resource with list of trending repositories
     */
    fun getTrendingRepositories(): Flow<Resource<List<Repository>>>
    
    /**
     * Get trending users with high follower counts.
     * @return Flow emitting Resource with list of trending users
     */
    fun getTrendingUsers(): Flow<Resource<List<User>>>
}
