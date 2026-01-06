package com.example.gitly.data.remote.api

import com.example.gitly.data.remote.dto.RepositoryDto
import com.example.gitly.data.remote.dto.RepositorySearchResponseDto
import com.example.gitly.data.remote.dto.UserDto
import com.example.gitly.data.remote.dto.UserSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * GitHub API Service interface for Retrofit.
 * Defines all API endpoints used in the application.
 */
interface GitHubApiService {
    
    companion object {
        const val BASE_URL = "https://api.github.com/"
    }
    
    /**
     * Search for users matching the query.
     */
    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): UserSearchResponseDto
    
    /**
     * Get detailed information about a specific user.
     */
    @GET("users/{username}")
    suspend fun getUserDetails(
        @Path("username") username: String
    ): UserDto
    
    /**
     * Search for repositories matching the query.
     */
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc"
    ): RepositorySearchResponseDto
    
    /**
     * Get repositories owned by a specific user.
     */
    @GET("users/{username}/repos")
    suspend fun getUserRepositories(
        @Path("username") username: String,
        @Query("per_page") perPage: Int = 30,
        @Query("sort") sort: String = "updated",
        @Query("direction") direction: String = "desc"
    ): List<RepositoryDto>
    
    /**
     * Get detailed information about a specific repository.
     */
    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): RepositoryDto
}
