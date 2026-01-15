package com.example.gitly.presentation.ui.screens.AI_Insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gitly.data.repository.GeminiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val geminiRepository: GeminiRepository
) : ViewModel() {
    
    private val _chatState = MutableStateFlow(AiChatState())
    val chatState: StateFlow<AiChatState> = _chatState.asStateFlow()
    
    init {
        // Add welcome message
        addWelcomeMessage()
    }
    
    private fun addWelcomeMessage() {
        val welcomeMessage = ChatMessage(
            id = generateMessageId(),
            content = """🐙 **Hey there! I'm Gitly AI** - your personal GitHub assistant!

I'm here to help you explore the GitHub universe. Here's what I can do:

📦 **Repositories** - Search, explore, and get insights
👤 **Developers** - Find profiles, contributions & experts  
📈 **Trending** - Discover what's hot right now
💡 **Insights** - Get smart analysis and recommendations

**Try asking me:**
• "Show me facebook/react"
• "Who is @torvalds?"
• "Find trending Kotlin repositories"
• "Search Python machine learning projects"

What would you like to explore today? 🚀""",
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
        
        // Call Gemini API for AI response
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
            
            // Call Gemini API
            val result = geminiRepository.generateResponse(userMessage)
            
            result.fold(
                onSuccess = { responseText ->
                    val aiResponse = ChatMessage(
                        id = generateMessageId(),
                        content = responseText,
                        isUser = false
                    )
                    // Replace loading message with actual response
                    _chatState.value = _chatState.value.copy(
                        messages = _chatState.value.messages.dropLast(1) + aiResponse,
                        isProcessing = false,
                        error = null
                    )
                },
                onFailure = { error ->
                    val errorMessage = ChatMessage(
                        id = generateMessageId(),
                        content = "Sorry, I encountered an error: ${error.message ?: "Unknown error"}. Please try again.",
                        isUser = false
                    )
                    // Replace loading message with error message
                    _chatState.value = _chatState.value.copy(
                        messages = _chatState.value.messages.dropLast(1) + errorMessage,
                        isProcessing = false,
                        error = error.message
                    )
                }
            )
        }
    }
    
    fun clearChat() {
        _chatState.value = AiChatState()
        addWelcomeMessage()
    }
    
    private fun generateMessageId(): String {
        return "msg_${System.currentTimeMillis()}_${(0..9999).random()}"
    }
}
