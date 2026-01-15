package com.example.gitly.data.repository

import com.example.gitly.data.remote.api.GeminiApiService
import com.example.gitly.data.remote.api.GitHubApiService
import com.example.gitly.data.remote.dto.GeminiContent
import com.example.gitly.data.remote.dto.GeminiPart
import com.example.gitly.data.remote.dto.GeminiRequestDto
import com.example.gitly.data.remote.dto.RepositoryDto
import com.example.gitly.data.remote.dto.UserDto
import com.example.gitly.data.remote.dto.getText
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for interacting with Gemini AI API
 * Enhanced with GitHub-specific context and real data fetching
 */
@Singleton
class GeminiRepository @Inject constructor(
    private val geminiApiService: GeminiApiService,
    private val gitHubApiService: GitHubApiService
) {
    
    companion object {
        private val SYSTEM_PROMPT = """
You are **Gitly AI** 🐙 — the ultimate GitHub assistant powering the Gitly mobile app. You are an expert in everything GitHub, open-source, and developer ecosystems. Your mission is to help users discover repositories, explore developer profiles, understand trends, and navigate the vast world of GitHub.

═══════════════════════════════════════════
                    IDENTITY
═══════════════════════════════════════════
• **Name:** Gitly AI
• **Role:** GitHub Expert & Code Explorer
• **Personality:** Friendly, knowledgeable, concise, enthusiastic about open-source
• **Tone:** Professional yet approachable, developer-friendly
• **Created by:** Gitly App Team

═══════════════════════════════════════════
              STRICT BOUNDARIES
═══════════════════════════════════════════
⚠️ **CRITICAL RULE: You ONLY discuss GitHub-related topics.**

✅ **YOU CAN HELP WITH:**
• GitHub repositories (search, explain, compare, recommend)
• GitHub users & developers (profiles, contributions, expertise)
• Organizations and teams on GitHub
• Programming languages (in GitHub context - popularity, repos, trends)
• Open-source projects, licenses, and communities
• Git concepts (commits, branches, PRs, issues, merge conflicts)
• Repository statistics (stars, forks, watchers, issues, releases)
• Trending repos & developers (daily, weekly, monthly)
• GitHub features (Actions, Pages, Packages, Gists, Discussions)
• Code topics, frameworks, and libraries on GitHub
• Developer tools and GitHub integrations
• Best practices for open-source contributions
• README files, documentation, and project structure
• Comparing similar repositories or technologies

❌ **YOU CANNOT HELP WITH (Politely Decline):**
• Weather, news, sports, entertainment
• Cooking, recipes, health, medical advice
• Math calculations, homework, general trivia
• Personal advice, relationships, life coaching
• Politics, religion, controversial topics
• Anything not directly related to GitHub/Git/Open-source

**When declining, ALWAYS respond with:**
"🤖 Hey there! I'm **Gitly AI**, your dedicated GitHub assistant!

I specialize exclusively in GitHub-related topics. I can't help with that, but I'd love to help you with:

📦 **Repositories** → Search, explore, compare projects
👤 **Developers** → Find profiles, top contributors
📈 **Trending** → Discover what's hot on GitHub
💡 **Insights** → Get recommendations & analysis

Try asking me something like:
• *'Show me facebook/react'*
• *'Who is @torvalds?'*
• *'Find trending Python projects'*

What GitHub topic can I help you explore? 🚀"

═══════════════════════════════════════════
              RESPONSE FORMATTING
═══════════════════════════════════════════

**General Rules:**
• Keep responses concise but complete (2-4 paragraphs max)
• Use markdown: **bold**, bullet points, line breaks
• Use emojis meaningfully (don't overuse)
• Format large numbers: 1.5k, 25.3k, 1.2M
• Always be accurate with provided [GITHUB DATA]
• End with a helpful follow-up suggestion

**Emoji Guide:**
• 📦 Repository
• 👤 User/Developer
• ⭐ Stars
• 🔀 Forks
• 🐛 Issues
• 💻 Language
• 📍 Location
• 📊 Statistics
• 🔥 Trending/Hot
• 🚀 Call to action
• 🐙 GitHub reference

**Repository Response Template:**
───────────────────────────────
📦 **{owner}/{repo}**
{description}

⭐ {stars} stars  •  🔀 {forks} forks  •  💻 {language}
🐛 {issues} open issues  •  📅 Updated: {date}

🏷️ **Topics:** {topics}

{brief analysis or interesting fact}

💡 **Want to explore more?** I can show you similar projects, top contributors, or compare with alternatives!
───────────────────────────────

**User Profile Response Template:**
───────────────────────────────
👤 **@{username}** ({name})
{bio}

📍 {location}  •  🏢 {company}
📊 {followers} followers  •  {following} following
📦 {public_repos} public repositories

🌟 **Highlights:** {interesting facts about the user}

💡 **Want to know more?** I can show their top repositories or find similar developers!
───────────────────────────────

**Search Results Template:**
───────────────────────────────
🔍 **Found {count} results for "{query}":**

1. **{repo1}** — {desc} | ⭐ {stars} | 💻 {lang}
2. **{repo2}** — {desc} | ⭐ {stars} | 💻 {lang}
3. **{repo3}** — {desc} | ⭐ {stars} | 💻 {lang}

💡 Want details on any of these? Just ask!
───────────────────────────────

═══════════════════════════════════════════
              SPECIAL BEHAVIORS
═══════════════════════════════════════════

**When [GITHUB DATA] is provided:**
• USE this real data — it's fetched live from GitHub API
• Present it beautifully using the templates above
• Add insights, comparisons, or interesting observations
• Never make up statistics — use only provided data

**When no data is provided:**
• Give general helpful information
• Suggest specific queries that would fetch real data
• Example: "Try asking 'Show me facebook/react' for live data!"

**For trending/popular queries:**
• Mention this is based on GitHub's current trends
• Suggest exploring by language or time period

**For comparisons:**
• Create clear side-by-side comparisons
• Highlight strengths of each option
• Recommend based on use case

═══════════════════════════════════════════
              PERSONALITY TOUCHES
═══════════════════════════════════════════

• Be enthusiastic about great open-source projects! 🎉
• Celebrate impressive stats ("Wow, 200k stars!")
• Show appreciation for contributors
• Be encouraging to new developers
• Add fun facts about popular repos when relevant
• Use phrases like:
  - "Great choice!"
  - "This is a fantastic project!"
  - "One of my favorites!"
  - "The community loves this one!"

═══════════════════════════════════════════
              EXAMPLE INTERACTIONS
═══════════════════════════════════════════

**User:** "What's the weather today?"
**Gitly AI:** [Use the polite decline response above]

**User:** "Tell me about tensorflow/tensorflow"
**Gitly AI:** 
📦 **tensorflow/tensorflow**
An open-source machine learning framework for everyone!

⭐ 180k+ stars  •  🔀 74k forks  •  💻 Python/C++
🐛 2.5k open issues  •  📅 Actively maintained

🏷️ **Topics:** machine-learning, deep-learning, neural-network

TensorFlow is Google's flagship ML framework, powering everything from research papers to production systems. It's one of the most starred repositories on GitHub! 🔥

💡 **Curious about alternatives?** I can compare it with PyTorch or show you TensorFlow's top contributors!

**User:** "Who is @gaborcsardi?"
**Gitly AI:**
👤 **@gaborcsardi** (Gábor Csárdi)
R package developer and open-source enthusiast

📍 Europe  •  🏢 Posit (RStudio)
📊 1.2k followers  •  150+ public repos

🌟 **Notable Work:** Creator of popular R packages including pak, cli, and crayon. A key contributor to the R ecosystem!

💡 **Want to see their repositories or find similar R developers?**

═══════════════════════════════════════════

Remember: You are Gitly AI — the friendliest, most helpful GitHub assistant! Stay focused, stay accurate, and help users explore the amazing world of open-source! 🐙✨
        """.trimIndent()
    }
    
    /**
     * Send a message to Gemini with GitHub context and get a response
     */
    suspend fun generateResponse(userMessage: String): Result<String> {
        return try {
            // Check if the query is about a specific repository or user
            val githubContext = fetchGitHubContext(userMessage)
            
            // Build the full prompt with system instructions and any fetched data
            val fullPrompt = buildPrompt(userMessage, githubContext)
            
            val request = GeminiRequestDto(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(
                            GeminiPart(text = fullPrompt)
                        )
                    )
                )
            )
            
            val response = geminiApiService.generateContent(request = request)
            val text = response.getText()
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun buildPrompt(userMessage: String, githubContext: GitHubContext?): String {
        val contextSection = when {
            githubContext?.repository != null -> {
                val repo = githubContext.repository
                """
                
[GITHUB DATA - Repository Found]:
- Full Name: ${repo.fullName}
- Description: ${repo.description ?: "No description"}
- Language: ${repo.language ?: "Not specified"}
- Stars: ${repo.stargazersCount ?: 0}
- Forks: ${repo.forksCount ?: 0}
- Open Issues: ${repo.openIssuesCount ?: 0}
- Owner: ${repo.owner.login}
- Topics: ${repo.topics?.joinToString(", ") ?: "None"}
- Created: ${repo.createdAt}
- Last Updated: ${repo.updatedAt}
- URL: ${repo.htmlUrl}
- Archived: ${repo.archived ?: false}

                """.trimIndent()
            }
            githubContext?.user != null -> {
                val user = githubContext.user
                """
                
[GITHUB DATA - User Found]:
- Username: ${user.login}
- Name: ${user.name ?: "Not provided"}
- Bio: ${user.bio ?: "No bio"}
- Location: ${user.location ?: "Not specified"}
- Company: ${user.company ?: "Not specified"}
- Public Repos: ${user.publicRepos}
- Followers: ${user.followers}
- Following: ${user.following}
- Blog: ${user.blog ?: "None"}
- Twitter: ${user.twitterUsername ?: "None"}
- Profile URL: ${user.htmlUrl}
- Member Since: ${user.createdAt}

                """.trimIndent()
            }
            githubContext?.repositories != null -> {
                val repos = githubContext.repositories.take(5)
                """
                
[GITHUB DATA - Search Results]:
${repos.mapIndexed { index, repo -> 
    "${index + 1}. **${repo.fullName}** - ${repo.description?.take(80) ?: "No description"} | ⭐ ${repo.stargazersCount} | ${repo.language ?: "Unknown"}"
}.joinToString("\n")}

                """.trimIndent()
            }
            githubContext?.users != null -> {
                val users = githubContext.users.take(5)
                """
                
[GITHUB DATA - Users Found]:
${users.mapIndexed { index, user -> 
    "${index + 1}. **@${user.login}** - ${user.name ?: user.login} | ${user.followers} followers"
}.joinToString("\n")}

                """.trimIndent()
            }
            else -> ""
        }
        
        return """
$SYSTEM_PROMPT

$contextSection

**User Question:** $userMessage

**Your Response:**
        """.trimIndent()
    }
    
    /**
     * Fetch relevant GitHub data based on the user's query
     */
    private suspend fun fetchGitHubContext(userMessage: String): GitHubContext? {
        val lowerMessage = userMessage.lowercase()
        
        return try {
            // Check for specific repository pattern (owner/repo)
            val repoPattern = Regex("""([a-zA-Z0-9_-]+)/([a-zA-Z0-9_.-]+)""")
            val repoMatch = repoPattern.find(userMessage)
            
            if (repoMatch != null) {
                val owner = repoMatch.groupValues[1]
                val repo = repoMatch.groupValues[2]
                try {
                    val repository = gitHubApiService.getRepository(owner, repo)
                    return GitHubContext(repository = repository)
                } catch (e: Exception) {
                    // Repository not found, continue with other checks
                }
            }
            
            // Check for @username pattern
            val userPattern = Regex("""@([a-zA-Z0-9_-]+)""")
            val userMatch = userPattern.find(userMessage)
            
            if (userMatch != null) {
                val username = userMatch.groupValues[1]
                try {
                    val user = gitHubApiService.getUserDetails(username)
                    return GitHubContext(user = user)
                } catch (e: Exception) {
                    // User not found, continue
                }
            }
            
            // Search for repositories if keywords suggest it
            val repoKeywords = listOf("repository", "repo", "project", "library", "framework", "find", "search", "show me", "trending")
            val userKeywords = listOf("developer", "user", "who is", "profile", "contributor", "author", "creator")
            
            when {
                repoKeywords.any { lowerMessage.contains(it) } -> {
                    // Extract search query - remove common words
                    val searchQuery = extractSearchQuery(userMessage, repoKeywords)
                    if (searchQuery.isNotBlank() && searchQuery.length > 2) {
                        val results = gitHubApiService.searchRepositories(searchQuery, perPage = 5)
                        if (results.items.isNotEmpty()) {
                            return GitHubContext(repositories = results.items)
                        }
                    }
                }
                userKeywords.any { lowerMessage.contains(it) } -> {
                    val searchQuery = extractSearchQuery(userMessage, userKeywords)
                    if (searchQuery.isNotBlank() && searchQuery.length > 2) {
                        val results = gitHubApiService.searchUsers(searchQuery, perPage = 5)
                        if (results.items.isNotEmpty()) {
                            return GitHubContext(users = results.items)
                        }
                    }
                }
            }
            
            null
        } catch (e: Exception) {
            null
        }
    }
    
    private fun extractSearchQuery(message: String, keywordsToRemove: List<String>): String {
        var query = message.lowercase()
        
        // Remove common stop words and keywords
        val stopWords = listOf(
            "show", "me", "find", "search", "for", "about", "the", "a", "an",
            "what", "is", "are", "tell", "give", "list", "get", "fetch",
            "repository", "repo", "repositories", "repos", "project", "projects",
            "developer", "developers", "user", "users", "who", "profile",
            "trending", "popular", "top", "best", "good", "great",
            "please", "can", "you", "i", "want", "need", "like", "to", "see"
        )
        
        stopWords.forEach { word ->
            query = query.replace(Regex("\\b$word\\b"), " ")
        }
        
        return query.trim().replace(Regex("\\s+"), " ")
    }
    
    /**
     * Generate AI summary for a GitHub user profile
     */
    suspend fun generateUserSummary(
        username: String,
        name: String?,
        bio: String?,
        location: String?,
        company: String?,
        blog: String?,
        twitterUsername: String?,
        publicRepos: Int,
        publicGists: Int,
        followers: Int,
        following: Int,
        createdAt: String?,
        repositories: List<RepoSummary>,
        totalContributions: Int?,
        languageStats: Map<String, LanguageStat>
    ): Result<String> {
        return try {
            val prompt = buildUserSummaryPrompt(
                username = username,
                name = name,
                bio = bio,
                location = location,
                company = company,
                blog = blog,
                twitterUsername = twitterUsername,
                publicRepos = publicRepos,
                publicGists = publicGists,
                followers = followers,
                following = following,
                createdAt = createdAt,
                repositories = repositories,
                totalContributions = totalContributions,
                languageStats = languageStats
            )
            
            val request = GeminiRequestDto(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(
                            GeminiPart(text = prompt)
                        )
                    )
                )
            )
            
            val response = geminiApiService.generateContent(request = request)
            val text = response.getText()
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun buildUserSummaryPrompt(
        username: String,
        name: String?,
        bio: String?,
        location: String?,
        company: String?,
        blog: String?,
        twitterUsername: String?,
        publicRepos: Int,
        publicGists: Int,
        followers: Int,
        following: Int,
        createdAt: String?,
        repositories: List<RepoSummary>,
        totalContributions: Int?,
        languageStats: Map<String, LanguageStat>
    ): String {
        val topRepos = repositories.sortedByDescending { it.stars }.take(5)
        val topLanguages = languageStats.entries.sortedByDescending { it.value.repoCount }.take(5)
        
        return """
You are Gitly AI, generating a professional and insightful summary for a GitHub developer profile.

═══════════════════════════════════════════
            USER PROFILE DATA
═══════════════════════════════════════════

👤 **Basic Information:**
- Username: @$username
- Display Name: ${name ?: "Not provided"}
- Bio: ${bio ?: "No bio available"}
- Location: ${location ?: "Not specified"}
- Company: ${company ?: "Not specified"}
- Website/Blog: ${blog ?: "None"}
- Twitter: ${if (twitterUsername != null) "@$twitterUsername" else "None"}
- Member Since: ${createdAt ?: "Unknown"}

📊 **Statistics:**
- Public Repositories: $publicRepos
- Public Gists: $publicGists
- Followers: $followers
- Following: $following
- Total Contributions (This Year): ${totalContributions ?: "Unknown"}

💻 **Programming Languages Used:**
${if (topLanguages.isNotEmpty()) {
    topLanguages.mapIndexed { index, (lang, stat) -> 
        "${index + 1}. $lang - ${stat.percentage}% (${stat.repoCount} repos)"
    }.joinToString("\n")
} else {
    "No language data available"
}}

📦 **Top Repositories (by stars):**
${if (topRepos.isNotEmpty()) {
    topRepos.mapIndexed { index, repo -> 
        "${index + 1}. **${repo.name}** - ${repo.description ?: "No description"}\n   ⭐ ${repo.stars} stars | 🔀 ${repo.forks} forks | 💻 ${repo.language ?: "Unknown"}"
    }.joinToString("\n\n")
} else {
    "No public repositories available"
}}

═══════════════════════════════════════════
            GENERATE SUMMARY
═══════════════════════════════════════════

Based on the above data, generate a comprehensive, engaging, and insightful AI summary for this developer. 

**Your summary should include:**

1. **Introduction** - A catchy one-liner about who this developer is
2. **Expertise Analysis** - What technologies/languages they specialize in based on their repos
3. **Activity Level** - How active they are (contributions, repo count, etc.)
4. **Notable Projects** - Highlight their best/most starred repositories
5. **Community Presence** - Follower ratio, influence in the community
6. **Developer Profile Type** - Categorize them (e.g., "Full-Stack Developer", "Open Source Enthusiast", "Mobile Developer", etc.)
7. **Fun Insight** - Something interesting or unique about their profile

**Formatting Rules:**
- Use emojis appropriately (🚀 💻 ⭐ 🔥 👨‍💻 etc.)
- Keep it concise but informative (200-300 words max)
- Use bold for emphasis
- Be enthusiastic and positive
- Make it feel personalized, not generic

**Example tone:**
"🚀 Meet @username - a passionate full-stack developer from San Francisco with a knack for building amazing open-source tools! With 50+ repositories and 1000+ followers, they've made quite an impact..."

Now generate the summary:
        """.trimIndent()
    }
    
    /**
     * Generate AI summary for a GitHub repository
     */
    suspend fun generateRepoSummary(
        fullName: String,
        name: String,
        ownerLogin: String,
        ownerType: String,
        description: String?,
        language: String?,
        stars: Int,
        forks: Int,
        watchers: Int,
        openIssues: Int,
        topics: List<String>?,
        createdAt: String?,
        updatedAt: String?,
        homepage: String?,
        isArchived: Boolean,
        isFork: Boolean,
        visibility: String?
    ): Result<String> {
        return try {
            val prompt = buildRepoSummaryPrompt(
                fullName = fullName,
                name = name,
                ownerLogin = ownerLogin,
                ownerType = ownerType,
                description = description,
                language = language,
                stars = stars,
                forks = forks,
                watchers = watchers,
                openIssues = openIssues,
                topics = topics,
                createdAt = createdAt,
                updatedAt = updatedAt,
                homepage = homepage,
                isArchived = isArchived,
                isFork = isFork,
                visibility = visibility
            )
            
            val request = GeminiRequestDto(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(
                            GeminiPart(text = prompt)
                        )
                    )
                )
            )
            
            val response = geminiApiService.generateContent(request = request)
            val text = response.getText()
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun buildRepoSummaryPrompt(
        fullName: String,
        name: String,
        ownerLogin: String,
        ownerType: String,
        description: String?,
        language: String?,
        stars: Int,
        forks: Int,
        watchers: Int,
        openIssues: Int,
        topics: List<String>?,
        createdAt: String?,
        updatedAt: String?,
        homepage: String?,
        isArchived: Boolean,
        isFork: Boolean,
        visibility: String?
    ): String {
        val formattedStars = formatNumber(stars)
        val formattedForks = formatNumber(forks)
        val formattedWatchers = formatNumber(watchers)
        
        return """
You are Gitly AI, generating a professional and insightful summary for a GitHub repository.

═══════════════════════════════════════════
            REPOSITORY DATA
═══════════════════════════════════════════

📦 **Basic Information:**
- Full Name: $fullName
- Repository Name: $name
- Owner: @$ownerLogin (${ownerType})
- Description: ${description ?: "No description provided"}
- Primary Language: ${language ?: "Not specified"}
- Visibility: ${visibility ?: "public"}
- Homepage/Website: ${homepage ?: "None"}
- Is Fork: $isFork
- Is Archived: $isArchived

📊 **Statistics:**
- ⭐ Stars: $stars ($formattedStars)
- 🔀 Forks: $forks ($formattedForks)
- 👁️ Watchers: $watchers ($formattedWatchers)
- 🐛 Open Issues: $openIssues

🏷️ **Topics/Tags:**
${if (!topics.isNullOrEmpty()) topics.joinToString(", ") else "No topics specified"}

📅 **Timeline:**
- Created: ${createdAt ?: "Unknown"}
- Last Updated: ${updatedAt ?: "Unknown"}

═══════════════════════════════════════════
            GENERATE SUMMARY
═══════════════════════════════════════════

Based on the above data, generate a comprehensive, engaging, and insightful AI summary for this repository.

**Your summary should include:**

1. **Introduction** - A catchy one-liner about what this repository is and why it matters
2. **Purpose & Use Case** - What problem does it solve? Who would use it?
3. **Technology Stack** - Based on language and topics, what technologies are involved?
4. **Popularity Analysis** - Analyze the stars, forks, and community engagement
5. **Project Health** - Based on issues count, last update, and activity indicators
6. **Unique Selling Points** - What makes this repo stand out?
7. **Recommendation** - Who should consider using/starring this repo?

**Special Considerations:**
${if (isArchived) "- ⚠️ This repository is ARCHIVED - mention this and what it means" else ""}
${if (isFork) "- 🔀 This is a FORK - mention this context appropriately" else ""}
${if (stars > 10000) "- 🔥 This is a HIGHLY POPULAR repository (10k+ stars) - emphasize its significance" else ""}
${if (stars > 50000) "- 🌟 This is an ELITE repository (50k+ stars) - one of the most starred on GitHub!" else ""}
${if (openIssues > 500) "- 📋 High number of open issues - could indicate active development or need for contributors" else ""}

**Formatting Rules:**
- Use emojis appropriately (🚀 💻 ⭐ 🔥 📦 🛠️ etc.)
- Keep it concise but informative (200-350 words max)
- Use bold for emphasis
- Be enthusiastic and informative
- Make it feel like a professional tech review
- Include a "verdict" or "bottom line" at the end

**Example tone:**
"🚀 **facebook/react** is the powerhouse behind modern web development! This JavaScript library has revolutionized how we build user interfaces, boasting an incredible 200k+ stars and powering millions of websites worldwide..."

Now generate the summary for this repository:
        """.trimIndent()
    }
    
    private fun formatNumber(num: Int): String {
        return when {
            num >= 1000000 -> String.format("%.1fM", num / 1000000.0)
            num >= 1000 -> String.format("%.1fk", num / 1000.0)
            else -> num.toString()
        }
    }
}

/**
 * Container for GitHub context data
 */
data class GitHubContext(
    val repository: RepositoryDto? = null,
    val user: UserDto? = null,
    val repositories: List<RepositoryDto>? = null,
    val users: List<UserDto>? = null
)

/**
 * Simple repo summary for AI prompt
 */
data class RepoSummary(
    val name: String,
    val description: String?,
    val stars: Int,
    val forks: Int,
    val language: String?
)

/**
 * Language statistics
 */
data class LanguageStat(
    val repoCount: Int,
    val percentage: Float
)
