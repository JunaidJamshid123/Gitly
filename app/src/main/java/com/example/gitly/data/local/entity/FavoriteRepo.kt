package com.example.gitly.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_repos")
data class FavoriteRepo(
    @PrimaryKey
    val id: Long,
    val name: String,
    val fullName: String,
    val ownerLogin: String,
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
    val archived: Boolean?,
    val fork: Boolean?,
    val savedAt: Long = System.currentTimeMillis()
)
