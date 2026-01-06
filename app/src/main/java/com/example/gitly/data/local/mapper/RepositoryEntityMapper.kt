package com.example.gitly.data.local.mapper

import com.example.gitly.data.local.entity.FavoriteRepositoryEntity
import com.example.gitly.domain.model.Repository
import com.example.gitly.domain.model.RepositoryOwner

/**
 * Mapper functions for Repository entities.
 */

fun FavoriteRepositoryEntity.toDomain(): Repository {
    return Repository(
        id = id,
        name = name,
        fullName = fullName,
        owner = RepositoryOwner(
            login = ownerLogin,
            id = ownerId,
            avatarUrl = ownerAvatarUrl,
            type = ownerType
        ),
        description = description,
        isPrivate = isPrivate,
        htmlUrl = htmlUrl,
        language = language,
        stargazersCount = stargazersCount,
        watchersCount = watchersCount,
        forksCount = forksCount,
        openIssuesCount = openIssuesCount,
        createdAt = createdAt,
        updatedAt = updatedAt,
        topics = topics?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
        visibility = visibility,
        homepage = homepage,
        isArchived = isArchived,
        isFork = isFork
    )
}

fun Repository.toEntity(): FavoriteRepositoryEntity {
    return FavoriteRepositoryEntity(
        id = id,
        name = name,
        fullName = fullName,
        ownerLogin = owner.login,
        ownerId = owner.id,
        ownerAvatarUrl = owner.avatarUrl,
        ownerType = owner.type,
        description = description,
        isPrivate = isPrivate,
        htmlUrl = htmlUrl,
        language = language,
        stargazersCount = stargazersCount,
        watchersCount = watchersCount,
        forksCount = forksCount,
        openIssuesCount = openIssuesCount,
        createdAt = createdAt,
        updatedAt = updatedAt,
        topics = topics.joinToString(","),
        visibility = visibility,
        homepage = homepage,
        isArchived = isArchived,
        isFork = isFork
    )
}

fun List<FavoriteRepositoryEntity>.toDomain(): List<Repository> {
    return map { it.toDomain() }
}
