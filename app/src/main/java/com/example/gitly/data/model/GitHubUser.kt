package com.example.gitly.data.model
data class GitHubUser(
    val login: String,
    val id: Int,
    val avatar_url: String,
    val url: String,
    val html_url: String,
    val type: String,
    val name: String? = null,
    val company: String? = null,
    val blog: String? = null,
    val location: String? = null,
    val email: String? = null,
    val bio: String? = null,
    val public_repos: Int = 0,
    val public_gists: Int = 0,
    val followers: Int = 0,
    val following: Int = 0,
    val created_at: String? = null,
    val updated_at: String? = null,
    val twitter_username: String? = null
)

data class UserSearchResponse(
    val total_count: Int,
    val incomplete_results: Boolean,
    val items: List<GitHubUser>
)
