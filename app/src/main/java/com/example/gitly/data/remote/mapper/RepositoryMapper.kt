package com.example.gitly.data.remote.mapper

import com.example.gitly.data.remote.dto.RepositoryDto
import com.example.gitly.data.remote.dto.RepositoryOwnerDto
import com.example.gitly.data.remote.dto.RepositorySearchResponseDto
import com.example.gitly.domain.model.Repository
import com.example.gitly.domain.model.RepositoryOwner
import com.example.gitly.domain.model.SearchResult

/**
 * Mapper functions to convert Repository DTOs to domain models.
 */

fun RepositoryOwnerDto.toDomain(): RepositoryOwner {
    return RepositoryOwner(
        login = login,
        id = id,
        avatarUrl = avatarUrl,
        type = type
    )
}

fun RepositoryDto.toDomain(): Repository {
    return Repository(
        id = id,
        name = name,
        fullName = fullName,
        owner = owner.toDomain(),
        description = description,
        isPrivate = isPrivate,
        htmlUrl = htmlUrl,
        language = language,
        stargazersCount = stargazersCount ?: 0,
        watchersCount = watchersCount ?: 0,
        forksCount = forksCount ?: 0,
        openIssuesCount = openIssuesCount ?: 0,
        createdAt = createdAt,
        updatedAt = updatedAt,
        topics = topics ?: emptyList(),
        visibility = visibility,
        homepage = homepage,
        isArchived = archived ?: false,
        isFork = fork ?: false
    )
}

fun List<RepositoryDto>.toDomain(): List<Repository> {
    return map { it.toDomain() }
}

fun RepositorySearchResponseDto.toDomain(): SearchResult<Repository> {
    return SearchResult(
        totalCount = totalCount,
        incompleteResults = incompleteResults,
        items = items.toDomain()
    )
}
