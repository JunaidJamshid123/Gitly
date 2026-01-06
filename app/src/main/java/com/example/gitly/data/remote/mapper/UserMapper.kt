package com.example.gitly.data.remote.mapper

import com.example.gitly.data.remote.dto.UserDto
import com.example.gitly.data.remote.dto.UserSearchResponseDto
import com.example.gitly.domain.model.SearchResult
import com.example.gitly.domain.model.User

/**
 * Mapper functions to convert User DTOs to domain models.
 */

fun UserDto.toDomain(): User {
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

fun List<UserDto>.toDomain(): List<User> {
    return map { it.toDomain() }
}

fun UserSearchResponseDto.toDomain(): SearchResult<User> {
    return SearchResult(
        totalCount = totalCount,
        incompleteResults = incompleteResults,
        items = items.toDomain()
    )
}
