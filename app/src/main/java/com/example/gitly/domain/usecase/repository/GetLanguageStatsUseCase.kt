package com.example.gitly.domain.usecase.repository

import com.example.gitly.domain.model.LanguageStats
import com.example.gitly.domain.model.SearchResult
import com.example.gitly.domain.model.Repository
import com.example.gitly.domain.repository.GitHubRepository
import com.example.gitly.domain.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for getting language statistics from GitHub.
 */
class GetLanguageStatsUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    private val languages = listOf(
        "JavaScript", "Python", "Java", "Kotlin",
        "TypeScript", "Go", "Rust", "Swift", "C++"
    )
    
    /**
     * Execute the use case.
     * @return Flow emitting Resource with list of language statistics
     */
    operator fun invoke(): Flow<Resource<List<LanguageStats>>> = flow {
        emit(Resource.Loading())
        
        try {
            val languageStats = mutableListOf<LanguageStats>()
            var totalRepos = 0
            
            coroutineScope {
                val deferredResults = languages.map { language ->
                    async {
                        val query = "language:${language.lowercase()}"
                        var count = 0
                        repository.searchRepositoriesWithCount(query, 1).collect { result ->
                            if (result is Resource.Success) {
                                count = result.data?.totalCount?.coerceAtMost(10_000_000) ?: 0
                            }
                        }
                        language to count
                    }
                }
                
                deferredResults.forEach { deferred ->
                    val (language, count) = deferred.await()
                    if (count > 0) {
                        totalRepos += count
                        languageStats.add(LanguageStats(language, 0f, "", count))
                    }
                }
            }
            
            if (totalRepos > 0) {
                val result = languageStats.map { stat ->
                    val percentage = (stat.repositoryCount.toFloat() / totalRepos.toFloat()) * 100f
                    stat.copy(percentage = percentage)
                }.sortedByDescending { it.repositoryCount }
                emit(Resource.Success(result))
            } else {
                emit(Resource.Success(getDefaultLanguageStats()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch language stats", getDefaultLanguageStats()))
        }
    }
    
    private fun getDefaultLanguageStats(): List<LanguageStats> {
        return listOf(
            LanguageStats("JavaScript", 44.0f, "+2.3%", 11_000_000),
            LanguageStats("Python", 20.0f, "+5.1%", 5_000_000),
            LanguageStats("Java", 12.0f, "+1.2%", 3_000_000),
            LanguageStats("Kotlin", 8.0f, "+7.3%", 2_000_000),
            LanguageStats("TypeScript", 8.0f, "+8.7%", 2_000_000),
            LanguageStats("Go", 4.0f, "+6.2%", 1_000_000),
            LanguageStats("Rust", 2.0f, "+12.4%", 500_000),
            LanguageStats("Swift", 1.0f, "+4.5%", 250_000),
            LanguageStats("C++", 1.0f, "+1.8%", 250_000)
        )
    }
}
