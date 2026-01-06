package com.example.gitly.domain.model

/**
 * Domain model representing a GitHub Repository.
 * This is a clean domain entity that is independent of any data layer implementation.
 */
data class Repository(
    val id: Long,
    val name: String,
    val fullName: String,
    val owner: RepositoryOwner,
    val description: String?,
    val isPrivate: Boolean,
    val htmlUrl: String,
    val language: String?,
    val stargazersCount: Int,
    val watchersCount: Int,
    val forksCount: Int,
    val openIssuesCount: Int,
    val createdAt: String?,
    val updatedAt: String?,
    val topics: List<String>,
    val visibility: String?,
    val homepage: String?,
    val isArchived: Boolean,
    val isFork: Boolean
) {
    // Convenience properties for accessing owner info
    val ownerLogin: String get() = owner.login
    val ownerAvatarUrl: String get() = owner.avatarUrl
    
    val formattedStars: String
        get() = formatCount(stargazersCount)
    
    val formattedForks: String
        get() = formatCount(forksCount)
    
    private fun formatCount(count: Int): String {
        return when {
            count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
            count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
            else -> count.toString()
        }
    }
}

/**
 * Domain model representing the owner of a repository.
 */
data class RepositoryOwner(
    val login: String,
    val id: Long,
    val avatarUrl: String,
    val type: String
)
