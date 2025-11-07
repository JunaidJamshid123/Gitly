package com.example.gitly.data.api

import com.example.gitly.data.model.GitHubRepo
import com.example.gitly.data.model.GitHubUser
import com.example.gitly.data.model.RepoSearchResponse
import com.example.gitly.data.model.UserSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApiService {
    
    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("per_page") perPage: Int = 30
    ): UserSearchResponse
    
    @GET("users/{username}")
    suspend fun getUserDetails(
        @Path("username") username: String
    ): GitHubUser
    
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("per_page") perPage: Int = 10
    ): RepoSearchResponse
    
    @GET("users/{username}/repos")
    suspend fun getUserRepositories(
        @Path("username") username: String,
        @Query("per_page") perPage: Int = 30,
        @Query("sort") sort: String = "updated"
    ): List<GitHubRepo>
    
    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): GitHubRepo
    
    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): GitHubUser
}
