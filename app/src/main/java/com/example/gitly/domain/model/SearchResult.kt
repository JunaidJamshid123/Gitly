package com.example.gitly.domain.model

/**
 * Generic search result wrapper containing total count and items.
 */
data class SearchResult<T>(
    val totalCount: Int,
    val incompleteResults: Boolean,
    val items: List<T>
)

/**
 * Language statistics domain model.
 */
data class LanguageStats(
    val name: String,
    val percentage: Float,
    val growth: String = "",
    val repositoryCount: Int = 0
)
