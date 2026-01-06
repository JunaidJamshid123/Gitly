package com.example.gitly.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for storing favorite users locally.
 */
@Entity(tableName = "favorite_users")
data class FavoriteUserEntity(
    @PrimaryKey
    val id: Int,
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String,
    val type: String,
    val name: String?,
    val company: String?,
    val blog: String?,
    val location: String?,
    val email: String?,
    val bio: String?,
    val publicRepos: Int,
    val publicGists: Int,
    val followers: Int,
    val following: Int,
    val createdAt: String?,
    val updatedAt: String?,
    val twitterUsername: String?,
    val savedAt: Long = System.currentTimeMillis()
)
