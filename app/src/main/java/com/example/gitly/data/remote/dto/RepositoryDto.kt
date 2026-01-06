package com.example.gitly.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for GitHub Repository from API response.
 */
data class RepositoryDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("owner")
    val owner: RepositoryOwnerDto,
    @SerializedName("description")
    val description: String?,
    @SerializedName("private")
    val isPrivate: Boolean,
    @SerializedName("html_url")
    val htmlUrl: String,
    @SerializedName("language")
    val language: String?,
    @SerializedName("stargazers_count")
    val stargazersCount: Int?,
    @SerializedName("watchers_count")
    val watchersCount: Int?,
    @SerializedName("forks_count")
    val forksCount: Int?,
    @SerializedName("open_issues_count")
    val openIssuesCount: Int?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("topics")
    val topics: List<String>?,
    @SerializedName("visibility")
    val visibility: String?,
    @SerializedName("homepage")
    val homepage: String?,
    @SerializedName("archived")
    val archived: Boolean?,
    @SerializedName("fork")
    val fork: Boolean?
)

/**
 * Repository owner DTO.
 */
data class RepositoryOwnerDto(
    @SerializedName("login")
    val login: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("avatar_url")
    val avatarUrl: String,
    @SerializedName("type")
    val type: String
)

/**
 * Repository search response from GitHub API.
 */
data class RepositorySearchResponseDto(
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean,
    @SerializedName("items")
    val items: List<RepositoryDto>
)
