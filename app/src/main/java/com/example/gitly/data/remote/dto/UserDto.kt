package com.example.gitly.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for GitHub User from API response.
 */
data class UserDto(
    @SerializedName("login")
    val login: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("avatar_url")
    val avatarUrl: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("html_url")
    val htmlUrl: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("company")
    val company: String? = null,
    @SerializedName("blog")
    val blog: String? = null,
    @SerializedName("location")
    val location: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("bio")
    val bio: String? = null,
    @SerializedName("public_repos")
    val publicRepos: Int = 0,
    @SerializedName("public_gists")
    val publicGists: Int = 0,
    @SerializedName("followers")
    val followers: Int = 0,
    @SerializedName("following")
    val following: Int = 0,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("twitter_username")
    val twitterUsername: String? = null
)

/**
 * User search response from GitHub API.
 */
data class UserSearchResponseDto(
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean,
    @SerializedName("items")
    val items: List<UserDto>
)
