package com.example.gitly.presentation.ui.screens.AI_Insights

/**
 * Example Queries for AI Assistant
 * 
 * Use these as examples when testing the AI chat feature.
 * The AI will provide formatted responses with clickable links.
 */

object ExampleQueries {
    
    // Repository Queries
    val REPOSITORY_QUERIES = listOf(
        "Show me trending React repositories",
        "Find popular Python projects",
        "What are the best machine learning repositories?",
        "Show me repositories about Android development",
        "Find popular TypeScript projects",
        "What are trending repositories today?"
    )
    
    // User/Developer Queries
    val DEVELOPER_QUERIES = listOf(
        "Find top Python developers",
        "Who are notable JavaScript developers?",
        "Show me active open-source contributors",
        "Find developers working on AI projects",
        "Who are the top contributors in the React community?"
    )
    
    // Trending Queries
    val TRENDING_QUERIES = listOf(
        "What's trending on GitHub?",
        "Show me trending topics today",
        "What are popular programming languages?",
        "What's hot in AI development?",
        "Show me trending data science projects"
    )
    
    // General Queries
    val GENERAL_QUERIES = listOf(
        "How can you help me?",
        "What can I search for?",
        "Tell me about GitHub statistics",
        "What features do you have?"
    )
    
    // All queries combined
    val ALL_EXAMPLE_QUERIES = REPOSITORY_QUERIES + DEVELOPER_QUERIES + TRENDING_QUERIES + GENERAL_QUERIES
    
    // Get a random example query
    fun getRandomQuery(): String {
        return ALL_EXAMPLE_QUERIES.random()
    }
    
    // Get queries by category
    fun getQueriesByCategory(category: String): List<String> {
        return when (category.lowercase()) {
            "repository", "repo" -> REPOSITORY_QUERIES
            "developer", "user" -> DEVELOPER_QUERIES
            "trending", "trend" -> TRENDING_QUERIES
            "general", "help" -> GENERAL_QUERIES
            else -> ALL_EXAMPLE_QUERIES
        }
    }
}
