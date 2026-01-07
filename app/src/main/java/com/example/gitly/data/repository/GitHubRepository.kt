package com.example.gitly.data.repository

import com.example.gitly.data.api.GraphQLClient
import com.example.gitly.data.api.RetrofitClient
import com.example.gitly.data.model.ContributionCalendar
import com.example.gitly.data.model.ContributionDay
import com.example.gitly.data.model.GitHubRepo
import com.example.gitly.data.model.GitHubUser
import com.example.gitly.data.model.RepoSearchResponse
import com.example.gitly.data.model.UserSearchResponse
import com.example.gitly.data.model.Week
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

class GitHubRepository {
    private val apiService = RetrofitClient.apiService
    private val graphQLService = GraphQLClient.apiService

    // Cache to store user details and search results
    private val userCache = mutableMapOf<String, Pair<GitHubUser, Long>>()
    private val repoCache = mutableMapOf<String, Pair<List<GitHubRepo>, Long>>()
    private val repoDetailsCache = mutableMapOf<String, Pair<GitHubRepo, Long>>()
    private val userSearchCache = mutableMapOf<String, Pair<List<GitHubUser>, Long>>()
    private val contributionCache = mutableMapOf<String, Pair<ContributionCalendar, Long>>()
    private val CACHE_DURATION = 15 * 60 * 1000L // 15 minutes
    private val CONTRIBUTION_CACHE_DURATION =
        60 * 60 * 1000L // 1 hour (contributions don't change frequently)
    private val MAX_CACHE_SIZE = 100 // Maximum items in each cache

    // Track ongoing requests to prevent duplicate simultaneous calls
    private val ongoingUserRequests = mutableMapOf<String, Deferred<Result<GitHubUser>>>()
    private val ongoingRepoRequests = mutableMapOf<String, Deferred<Result<GitHubRepo>>>()

    suspend fun searchUsers(query: String): Result<List<GitHubUser>> {
        return try {
            // Check cache first
            val cached = userSearchCache[query]
            if (cached != null && System.currentTimeMillis() - cached.second < CACHE_DURATION) {
                return Result.success(cached.first)
            }

            // Search for users (fetch 100 for pagination)
            // Return basic search results without fetching individual details for all users
            // to avoid rate limiting
            val searchResponse = apiService.searchUsers(query, perPage = 100)

            // Manage cache size
            if (userSearchCache.size >= MAX_CACHE_SIZE) {
                val oldestKey = userSearchCache.minByOrNull { it.value.second }?.key
                oldestKey?.let { userSearchCache.remove(it) }
            }

            // Cache the results
            userSearchCache[query] = Pair(searchResponse.items, System.currentTimeMillis())

            Result.success(searchResponse.items)
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

            // Check if there's already an ongoing request for this user
            ongoingUserRequests[username]?.let {
                return it.await()
            }

            // Create and track the request
            val deferred = coroutineScope {
                async {
                    try {
                        val user = apiService.getUserDetails(username)
                        // Manage cache size
                        if (userCache.size >= MAX_CACHE_SIZE) {
                            val oldestKey = userCache.minByOrNull { it.value.second }?.key
                            oldestKey?.let { userCache.remove(it) }
                        }
                        userCache[username] = Pair(user, System.currentTimeMillis())
                        Result.success(user)
                    } catch (e: Exception) {
                        Result.failure(e)
                    }
                }
            }
            ongoingUserRequests[username] = deferred
            val result = deferred.await()
            ongoingUserRequests.remove(username)
            result
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

            // Search for repositories (fetch 100 for pagination)
            val searchResponse = apiService.searchRepositories(query, perPage = 100)

            // Manage cache size
            if (repoCache.size >= MAX_CACHE_SIZE) {
                val oldestKey = repoCache.minByOrNull { it.value.second }?.key
                oldestKey?.let { repoCache.remove(it) }
            }

            // Cache the results
            repoCache[query] = Pair(searchResponse.items, System.currentTimeMillis())

            Result.success(searchResponse.items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchRepositoriesWithCount(
        query: String,
        perPage: Int = 1
    ): Result<RepoSearchResponse> {
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

            // Manage cache size
            if (repoCache.size >= MAX_CACHE_SIZE) {
                val oldestKey = repoCache.minByOrNull { it.value.second }?.key
                oldestKey?.let { repoCache.remove(it) }
            }

            // Cache the results
            repoCache[cacheKey] = Pair(repos, System.currentTimeMillis())

            Result.success(repos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRepositoryDetails(owner: String, repo: String): Result<GitHubRepo> {
        return try {
            val cacheKey = "$owner/$repo"

            // Check cache first
            val cached = repoDetailsCache[cacheKey]
            if (cached != null && System.currentTimeMillis() - cached.second < CACHE_DURATION) {
                return Result.success(cached.first)
            }

            // Check if there's already an ongoing request
            ongoingRepoRequests[cacheKey]?.let {
                return it.await()
            }

            // Create and track the request
            val deferred = coroutineScope {
                async {
                    try {
                        val repoDetails = apiService.getRepository(owner, repo)
                        // Manage cache size
                        if (repoDetailsCache.size >= MAX_CACHE_SIZE) {
                            val oldestKey = repoDetailsCache.minByOrNull { it.value.second }?.key
                            oldestKey?.let { repoDetailsCache.remove(it) }
                        }
                        repoDetailsCache[cacheKey] = Pair(repoDetails, System.currentTimeMillis())
                        Result.success(repoDetails)
                    } catch (e: Exception) {
                        Result.failure(e)
                    }
                }
            }
            ongoingRepoRequests[cacheKey] = deferred
            val result = deferred.await()
            ongoingRepoRequests.remove(cacheKey)
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserContributions(username: String): Result<ContributionCalendar> {
        return try {
            // Check cache first
            val cached = contributionCache[username]
            if (cached != null && System.currentTimeMillis() - cached.second < CONTRIBUTION_CACHE_DURATION) {
                return Result.success(cached.first)
            }

            // Fetch from GraphQL API
            val query = GraphQLClient.buildContributionQuery(username)
            val response = graphQLService.query(query)

            val rawCalendar = response.data.user?.contributionsCollection?.contributionCalendar

            if (rawCalendar != null) {
                // Map raw response to processed model with computed levels
                val processedCalendar = ContributionCalendar(
                    totalContributions = rawCalendar.totalContributions,
                    weeks = rawCalendar.weeks.map { rawWeek ->
                        Week(
                            contributionDays = rawWeek.contributionDays.map { rawDay ->
                                ContributionDay(
                                    date = rawDay.date,
                                    contributionCount = rawDay.contributionCount,
                                    weekday = rawDay.weekday,
                                    level = getContributionLevel(rawDay.contributionCount),
                                    color = rawDay.color
                                )
                            }
                        )
                    }
                )

                // Manage cache size
                if (contributionCache.size >= MAX_CACHE_SIZE) {
                    val oldestKey = contributionCache.minByOrNull { it.value.second }?.key
                    oldestKey?.let { contributionCache.remove(it) }
                }

                // Cache the result
                contributionCache[username] = Pair(processedCalendar, System.currentTimeMillis())

                Result.success(processedCalendar)
            } else {
                Result.failure(Exception("User not found or contributions unavailable"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getContributionLevel(count: Int): Int {
        return when {
            count == 0 -> 0
            count in 1..3 -> 1
            count in 4..6 -> 2
            count in 7..9 -> 3
            else -> 4
        }
    }
}
