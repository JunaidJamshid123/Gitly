package com.example.gitly.data.local.mapper

import com.example.gitly.data.local.entity.FavoriteUserEntity
import com.example.gitly.domain.model.User

/**
 * Mapper functions for User entities.
 */

fun FavoriteUserEntity.toDomain(): User {
    return User(
        id = id,
        login = login,
        avatarUrl = avatarUrl,
        htmlUrl = htmlUrl,
        type = type,
        name = name,
        company = company,
        blog = blog,
        location = location,
        email = email,
        bio = bio,
        publicRepos = publicRepos,
        publicGists = publicGists,
        followers = followers,
        following = following,
        createdAt = createdAt,
        updatedAt = updatedAt,
        twitterUsername = twitterUsername
    )
}

fun User.toEntity(): FavoriteUserEntity {
    return FavoriteUserEntity(
        id = id,
        login = login,
        avatarUrl = avatarUrl,
        htmlUrl = htmlUrl,
        type = type,
        name = name,
        company = company,
        blog = blog,
        location = location,
        email = email,
        bio = bio,
        publicRepos = publicRepos,
        publicGists = publicGists,
        followers = followers,
        following = following,
        createdAt = createdAt,
        updatedAt = updatedAt,
        twitterUsername = twitterUsername
    )
}

fun List<FavoriteUserEntity>.toDomain(): List<User> {
    return map { it.toDomain() }
}
