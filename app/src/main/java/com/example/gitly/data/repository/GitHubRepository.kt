package com.example.gitly.data.repository

import com.example.gitly.data.api.RetrofitClient
import com.example.gitly.data.model.GitHubRepo
import com.example.gitly.data.model.GitHubUser
import com.example.gitly.data.model.RepoSearchResponse
import com.example.gitly.data.model.UserSearchResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

class GitHubRepository {
    private val apiService = RetrofitClient.apiService
    
    // Cache to store user details
    private val userCache = mutableMapOf<String, Pair<GitHubUser, Long>>()
    private val repoCache = mutableMapOf<String, Pair<List<GitHubRepo>, Long>>()
    private val CACHE_DURATION = 5 * 60 * 1000L // 5 minutes
    
    suspend fun searchUsers(query: String): Result<List<GitHubUser>> {
        return try {
            // First, search for users (limit to 10 for fewer API calls)
            val searchResponse = apiService.searchUsers(query, perPage = 10)
            
            // Then, fetch full details for each user with rate limiting
            val detailedUsers = coroutineScope {
                searchResponse.items.mapIndexed { index, user ->
                    async {
                        try {
                            // Add delay between requests to avoid rate limiting
                            if (index > 0) {
                                delay(200L) // 200ms delay between each request
                            }
                            
                            // Check cache first
                            val cached = userCache[user.login]
                            if (cached != null && System.currentTimeMillis() - cached.second < CACHE_DURATION) {
                                cached.first
                            } else {
                                // Fetch from API and cache it
                                val userDetails = apiService.getUserDetails(user.login)
                                userCache[user.login] = Pair(userDetails, System.currentTimeMillis())
                                userDetails
                            }
                        } catch (e: Exception) {
                            // If details fetch fails, return the basic user info
                            user
                        }
                    }
                }.awaitAll()
            }
            
            Result.success(detailedUsers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserDetails(username: String): Result<GitHubUser> {
        return try {
            // Check cache first
            val cached = userCache[username]
            if (cached != null && System.currentTimeMillis() - cached.second < CACHE_DURATION) {
                return Result.success(cached.first)
            }
            
            // Fetch from API and cache it
            val user = apiService.getUserDetails(username)
            userCache[username] = Pair(user, System.currentTimeMillis())
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun searchRepositories(query: String): Result<List<GitHubRepo>> {
        return try {
            // Check cache first
            val cached = repoCache[query]
            if (cached != null && System.currentTimeMillis() - cached.second < CACHE_DURATION) {
                return Result.success(cached.first)
            }
            
            // Search for repositories
            val searchResponse = apiService.searchRepositories(query, perPage = 10)
            
            // Cache the results
            repoCache[query] = Pair(searchResponse.items, System.currentTimeMillis())
            
            Result.success(searchResponse.items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun searchRepositoriesWithCount(query: String, perPage: Int = 1): Result<RepoSearchResponse> {
        return try {
            // Search for repositories with full response including total count
            val searchResponse = apiService.searchRepositories(query, perPage = perPage)
            Result.success(searchResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserRepositories(username: String): Result<List<GitHubRepo>> {
        return try {
            // Check cache first
            val cacheKey = "user_repos_$username"
            val cached = repoCache[cacheKey]
            if (cached != null && System.currentTimeMillis() - cached.second < CACHE_DURATION) {
                return Result.success(cached.first)
            }
            
            // Fetch user repositories
            val repos = apiService.getUserRepositories(username, perPage = 30, sort = "updated")
            
            // Cache the results
            repoCache[cacheKey] = Pair(repos, System.currentTimeMillis())
            
            Result.success(repos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getRepositoryDetails(owner: String, repo: String): Result<GitHubRepo> {
        return try {
            val repoDetails = apiService.getRepository(owner, repo)
            Result.success(repoDetails)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
