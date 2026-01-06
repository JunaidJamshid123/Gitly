package com.example.gitly.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for storing favorite repositories locally.
 */
@Entity(tableName = "favorite_repositories")
data class FavoriteRepositoryEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val fullName: String,
    val ownerLogin: String,
    val ownerId: Long,
    val ownerAvatarUrl: String,
    val ownerType: String,
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
    val topics: String?, // Stored as comma-separated values
    val visibility: String?,
    val homepage: String?,
    val isArchived: Boolean,
    val isFork: Boolean,
    val savedAt: Long = System.currentTimeMillis()
)
