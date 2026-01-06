package com.example.gitly.domain.model

/**
 * Domain model representing a GitHub User.
 * This is a clean domain entity that is independent of any data layer implementation.
 */
data class User(
    val id: Int,
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String,
    val type: String,
    val name: String? = null,
    val company: String? = null,
    val blog: String? = null,
    val location: String? = null,
    val email: String? = null,
    val bio: String? = null,
    val publicRepos: Int = 0,
    val publicGists: Int = 0,
    val followers: Int = 0,
    val following: Int = 0,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val twitterUsername: String? = null
) {
    val displayName: String
        get() = name ?: login
    
    val hasDetailedInfo: Boolean
        get() = name != null || bio != null || company != null
}
