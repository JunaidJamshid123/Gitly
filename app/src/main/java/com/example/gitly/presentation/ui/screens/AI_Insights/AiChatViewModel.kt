package com.example.gitly.presentation.ui.screens.AI_Insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val links: List<MessageLink> = emptyList(),
    val isLoading: Boolean = false
)

data class MessageLink(
    val text: String,
    val url: String,
    val type: LinkType
)

enum class LinkType {
    REPOSITORY,
    USER,
    EXTERNAL
}

data class AiChatState(
    val messages: List<ChatMessage> = emptyList(),
    val isProcessing: Boolean = false,
    val error: String? = null
)

class AiChatViewModel : ViewModel() {
    
    private val _chatState = MutableStateFlow(AiChatState())
    val chatState: StateFlow<AiChatState> = _chatState.asStateFlow()
    
    init {
        // Add welcome message
        addWelcomeMessage()
    }
    
    private fun addWelcomeMessage() {
        val welcomeMessage = ChatMessage(
            id = generateMessageId(),
            content = "👋 Hello! I'm your AI assistant for GitHub insights. I can help you discover repositories, find developers, analyze trends, and answer questions about the GitHub ecosystem.\n\nWhat would you like to know?",
            isUser = false
        )
        _chatState.value = _chatState.value.copy(
            messages = listOf(welcomeMessage)
        )
    }
    
    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return
        
        // Add user message
        val userChatMessage = ChatMessage(
            id = generateMessageId(),
            content = userMessage.trim(),
            isUser = true
        )
        
        _chatState.value = _chatState.value.copy(
            messages = _chatState.value.messages + userChatMessage,
            isProcessing = true
        )
        
        // Simulate AI response
        viewModelScope.launch {
            // Add loading message
            val loadingMessage = ChatMessage(
                id = generateMessageId(),
                content = "",
                isUser = false,
                isLoading = true
            )
            _chatState.value = _chatState.value.copy(
                messages = _chatState.value.messages + loadingMessage
            )
            
            // Simulate processing delay
            delay(1500)
            
            // Generate AI response
            val aiResponse = generateAiResponse(userMessage)
            
            // Replace loading message with actual response
            _chatState.value = _chatState.value.copy(
                messages = _chatState.value.messages.dropLast(1) + aiResponse,
                isProcessing = false
            )
        }
    }
    
    private fun generateAiResponse(userQuery: String): ChatMessage {
        // This is a placeholder for actual AI integration
        // You would integrate with OpenAI, Gemini, or another AI service here
        
        val response = when {
            userQuery.contains("repository", ignoreCase = true) || 
            userQuery.contains("repo", ignoreCase = true) -> {
                generateRepositoryResponse(userQuery)
            }
            userQuery.contains("user", ignoreCase = true) || 
            userQuery.contains("developer", ignoreCase = true) -> {
                generateUserResponse(userQuery)
            }
            userQuery.contains("trending", ignoreCase = true) -> {
                generateTrendingResponse(userQuery)
            }
            else -> {
                generateGeneralResponse(userQuery)
            }
        }
        
        return response
    }
    
    private fun generateRepositoryResponse(query: String): ChatMessage {
        val content = """
            Based on your query about repositories, here are some insights:
            
            🔍 **Search Results:**
            I found several relevant repositories that might interest you. Here are some top matches:
            
            1. **facebook/react** - A JavaScript library for building user interfaces
               ⭐ 220k stars | 🔀 45k forks
               
            2. **microsoft/vscode** - Visual Studio Code
               ⭐ 155k stars | 🔀 27k forks
               
            3. **tensorflow/tensorflow** - An Open Source Machine Learning Framework
               ⭐ 180k stars | 🔀 74k forks
            
            💡 **Tips:**
            • You can click on any repository link to view more details
            • Use filters to narrow down your search
            • Star repositories you find interesting for future reference
            
            Would you like me to provide more specific information about any of these repositories?
        """.trimIndent()
        
        val links = listOf(
            MessageLink("facebook/react", "facebook/react", LinkType.REPOSITORY),
            MessageLink("microsoft/vscode", "microsoft/vscode", LinkType.REPOSITORY),
            MessageLink("tensorflow/tensorflow", "tensorflow/tensorflow", LinkType.REPOSITORY)
        )
        
        return ChatMessage(
            id = generateMessageId(),
            content = content,
            isUser = false,
            links = links
        )
    }
    
    private fun generateUserResponse(query: String): ChatMessage {
        val content = """
            Here are some notable GitHub developers based on your query:
            
            👥 **Top Developers:**
            
            1. **@torvalds** - Linus Torvalds
               Creator of Linux and Git
               📍 Portland, OR
               
            2. **@gaearon** - Dan Abramov
               Co-author of Redux, Create React App
               📍 London, UK
               
            3. **@sindresorhus** - Sindre Sorhus
               Full-time open-source maintainer
               📍 Norway
            
            🌟 **Their Contributions:**
            • Combined 500+ repositories
            • Millions of stars across projects
            • Active in open-source community
            
            Click on any developer to view their complete profile and repositories!
        """.trimIndent()
        
        val links = listOf(
            MessageLink("@torvalds", "torvalds", LinkType.USER),
            MessageLink("@gaearon", "gaearon", LinkType.USER),
            MessageLink("@sindresorhus", "sindresorhus", LinkType.USER)
        )
        
        return ChatMessage(
            id = generateMessageId(),
            content = content,
            isUser = false,
            links = links
        )
    }
    
    private fun generateTrendingResponse(query: String): ChatMessage {
        val content = """
            📈 **Trending on GitHub Today:**
            
            Here's what's hot in the developer community:
            
            **🔥 Trending Repositories:**
            1. awesome-chatgpt-prompts - Curated ChatGPT prompts
            2. stable-diffusion-webui - Stable Diffusion web UI
            3. llama.cpp - LLM inference in C/C++
            
            **💻 Trending Languages:**
            • TypeScript - 🔺 15% increase
            • Python - 🔺 12% increase
            • Rust - 🔺 18% increase
            
            **📊 Key Statistics:**
            • 1.2M+ new repositories this month
            • 85% increase in AI/ML projects
            • 60% developers prefer remote collaboration
            
            Want to explore any specific trending topic in detail?
        """.trimIndent()
        
        return ChatMessage(
            id = generateMessageId(),
            content = content,
            isUser = false
        )
    }
    
    private fun generateGeneralResponse(query: String): ChatMessage {
        val content = """
            I can help you with various GitHub-related queries! Here are some things I can do:
            
            🔍 **Search & Discovery:**
            • Find repositories by topic or language
            • Discover trending projects
            • Search for developers and organizations
            
            📊 **Analysis & Insights:**
            • Repository statistics and metrics
            • Contributor analysis
            • Technology trend reports
            
            💡 **Recommendations:**
            • Project suggestions based on interests
            • Developer collaboration opportunities
            • Learning resources
            
            🔗 **Direct Navigation:**
            • Quick access to repositories
            • User profile exploration
            • Organization overviews
            
            **Try asking me:**
            • "Show me React repositories"
            • "Who are top Python developers?"
            • "What's trending in AI?"
            • "Find machine learning projects"
            
            What specific information are you looking for?
        """.trimIndent()
        
        return ChatMessage(
            id = generateMessageId(),
            content = content,
            isUser = false
        )
    }
    
    fun clearChat() {
        _chatState.value = AiChatState()
        addWelcomeMessage()
    }
    
    private fun generateMessageId(): String {
        return "msg_${System.currentTimeMillis()}_${(0..9999).random()}"
    }
}
