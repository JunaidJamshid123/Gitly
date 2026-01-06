package com.example.gitly.data.repository

import com.example.gitly.data.remote.api.GitHubApiService
import com.example.gitly.data.remote.mapper.toDomain
import com.example.gitly.domain.model.Repository
import com.example.gitly.domain.model.SearchResult
import com.example.gitly.domain.model.User
import com.example.gitly.domain.repository.GitHubRepository
import com.example.gitly.domain.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of GitHubRepository.
 * This class handles all GitHub API interactions and caching.
 */
@Singleton
class GitHubRepositoryImpl @Inject constructor(
    private val apiService: GitHubApiService
) : GitHubRepository {
    
    // In-memory cache with timestamps
    private val userCache = mutableMapOf<String, Pair<User, Long>>()
    private val repoCache = mutableMapOf<String, Pair<List<Repository>, Long>>()
    
    companion object {
        private const val CACHE_DURATION_MS = 5 * 60 * 1000L // 5 minutes
    }
    
    override fun searchUsers(query: String): Flow<Resource<List<User>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.searchUsers(query, perPage = 100)
            emit(Resource.Success(response.items.toDomain()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to search users"))
        }
    }.flowOn(Dispatchers.IO)
    
    override fun getUserDetails(username: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            // Check cache first
            val cached = userCache[username]
            if (cached != null && System.currentTimeMillis() - cached.second < CACHE_DURATION_MS) {
                emit(Resource.Success(cached.first))
                return@flow
            }
            
            // Fetch from API
            val user = apiService.getUserDetails(username).toDomain()
            userCache[username] = Pair(user, System.currentTimeMillis())
            emit(Resource.Success(user))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to get user details"))
        }
    }.flowOn(Dispatchers.IO)
    
    override fun searchRepositories(query: String): Flow<Resource<List<Repository>>> = flow {
        emit(Resource.Loading())
        try {
            // Check cache first
            val cached = repoCache[query]
            if (cached != null && System.currentTimeMillis() - cached.second < CACHE_DURATION_MS) {
                emit(Resource.Success(cached.first))
                return@flow
            }
            
            val response = apiService.searchRepositories(query, perPage = 100)
            val repos = response.items.toDomain()
            repoCache[query] = Pair(repos, System.currentTimeMillis())
            emit(Resource.Success(repos))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to search repositories"))
        }
    }.flowOn(Dispatchers.IO)
    
    override fun searchRepositoriesWithCount(
        query: String,
        perPage: Int
    ): Flow<Resource<SearchResult<Repository>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.searchRepositories(query, perPage = perPage)
            emit(Resource.Success(response.toDomain()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to search repositories"))
        }
    }.flowOn(Dispatchers.IO)
    
    override fun getUserRepositories(username: String): Flow<Resource<List<Repository>>> = flow {
        emit(Resource.Loading())
        try {
            val cacheKey = "user_repos_$username"
            val cached = repoCache[cacheKey]
            if (cached != null && System.currentTimeMillis() - cached.second < CACHE_DURATION_MS) {
                emit(Resource.Success(cached.first))
                return@flow
            }
            
            val repos = apiService.getUserRepositories(username, perPage = 30).toDomain()
            repoCache[cacheKey] = Pair(repos, System.currentTimeMillis())
            emit(Resource.Success(repos))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to get user repositories"))
        }
    }.flowOn(Dispatchers.IO)
    
    override fun getRepositoryDetails(owner: String, repo: String): Flow<Resource<Repository>> = flow {
        emit(Resource.Loading())
        try {
            val repository = apiService.getRepository(owner, repo).toDomain()
            emit(Resource.Success(repository))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to get repository details"))
        }
    }.flowOn(Dispatchers.IO)
    
    override fun getTrendingRepositories(): Flow<Resource<List<Repository>>> = flow {
        emit(Resource.Loading())
        try {
            // Get repositories created in the last 7 days, sorted by stars
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -7)
            val weekAgo = dateFormat.format(calendar.time)
            
            val query = "created:>$weekAgo stars:>100"
            val response = apiService.searchRepositories(query, perPage = 20)
            emit(Resource.Success(response.items.toDomain()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to get trending repositories"))
        }
    }.flowOn(Dispatchers.IO)
    
    override fun getTrendingUsers(): Flow<Resource<List<User>>> = flow {
        emit(Resource.Loading())
        try {
            // Get users with high follower count
            val query = "followers:>10000"
            val searchResponse = apiService.searchUsers(query, perPage = 10)
            
            // Fetch detailed info for top users
            val detailedUsers = coroutineScope {
                searchResponse.items.take(5).map { userDto ->
                    async {
                        try {
                            apiService.getUserDetails(userDto.login).toDomain()
                        } catch (e: Exception) {
                            // Fallback to basic info
                            userDto.toDomain()
                        }
                    }
                }.awaitAll()
            }
            
            emit(Resource.Success(detailedUsers))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to get trending users"))
        }
    }.flowOn(Dispatchers.IO)
}
