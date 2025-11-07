package com.example.gitly.data.model

data class UserStatistics(
    val username: String,
    val avatarUrl: String,
    val name: String?,
    val totalRepos: Int,
    val totalStars: Int,
    val totalForks: Int,
    val followers: Int,
    val following: Int,
    val accountAge: String,
    val languageStats: Map<String, Int>,
    val topRepositories: List<TopRepository>,
    val totalContributions: Int = 0
)

data class TopRepository(
    val name: String,
    val stars: Int,
    val forks: Int,
    val language: String?
)

data class RepositoryStatistics(
    val name: String,
    val fullName: String,
    val description: String?,
    val stars: Int,
    val forks: Int,
    val watchers: Int,
    val openIssues: Int,
    val size: Int,
    val language: String?,
    val languageStats: Map<String, Int>,
    val createdAt: String,
    val updatedAt: String,
    val contributorsCount: Int = 0,
    val commitsCount: Int = 0
)
