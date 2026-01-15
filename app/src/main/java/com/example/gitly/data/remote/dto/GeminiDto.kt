package com.example.gitly.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Request DTO for Gemini API
 */
data class GeminiRequestDto(
    @SerializedName("contents")
    val contents: List<GeminiContent>
)

data class GeminiContent(
    @SerializedName("parts")
    val parts: List<GeminiPart>,
    @SerializedName("role")
    val role: String? = null
)

data class GeminiPart(
    @SerializedName("text")
    val text: String
)

/**
 * Response DTO for Gemini API
 */
data class GeminiResponseDto(
    @SerializedName("candidates")
    val candidates: List<GeminiCandidate>?,
    @SerializedName("usageMetadata")
    val usageMetadata: GeminiUsageMetadata?,
    @SerializedName("modelVersion")
    val modelVersion: String?,
    @SerializedName("responseId")
    val responseId: String?
)

data class GeminiCandidate(
    @SerializedName("content")
    val content: GeminiCandidateContent?,
    @SerializedName("finishReason")
    val finishReason: String?,
    @SerializedName("index")
    val index: Int?
)

data class GeminiCandidateContent(
    @SerializedName("parts")
    val parts: List<GeminiResponsePart>?,
    @SerializedName("role")
    val role: String?
)

data class GeminiResponsePart(
    @SerializedName("text")
    val text: String?,
    @SerializedName("thoughtSignature")
    val thoughtSignature: String?
)

data class GeminiUsageMetadata(
    @SerializedName("promptTokenCount")
    val promptTokenCount: Int?,
    @SerializedName("candidatesTokenCount")
    val candidatesTokenCount: Int?,
    @SerializedName("totalTokenCount")
    val totalTokenCount: Int?,
    @SerializedName("thoughtsTokenCount")
    val thoughtsTokenCount: Int?
)

// Extension function to easily extract text from response
fun GeminiResponseDto.getText(): String {
    return candidates?.firstOrNull()
        ?.content?.parts?.firstOrNull { !it.text.isNullOrBlank() }
        ?.text ?: "Sorry, I couldn't generate a response."
}
