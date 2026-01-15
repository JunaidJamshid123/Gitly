package com.example.gitly.data.remote.api

import com.example.gitly.BuildConfig
import com.example.gitly.data.remote.dto.GeminiRequestDto
import com.example.gitly.data.remote.dto.GeminiResponseDto
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Gemini API Service interface for Retrofit.
 * Used for AI-powered chat responses in the AI Insights screen.
 */
interface GeminiApiService {
    
    companion object {
        const val BASE_URL = "https://generativelanguage.googleapis.com/"
        // API key is loaded from BuildConfig (set via local.properties)
        val API_KEY: String get() = BuildConfig.GEMINI_API_KEY
    }
    
    @POST("v1beta/models/gemini-3-flash-preview:generateContent")
    suspend fun generateContent(
        @Header("x-goog-api-key") apiKey: String = API_KEY,
        @Body request: GeminiRequestDto
    ): GeminiResponseDto
}
