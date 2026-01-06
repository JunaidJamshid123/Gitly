package com.example.gitly.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Interceptor to add common headers to all API requests.
 * Adds Accept header for GitHub API v3 and optional authentication.
 */
class AuthInterceptor @Inject constructor() : Interceptor {
    
    // For production, you would inject this from a secure source
    private var authToken: String? = null
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val requestBuilder = originalRequest.newBuilder()
            .header("Accept", "application/vnd.github.v3+json")
            .header("User-Agent", "Gitly-Android-App")
        
        // Add authorization header if token is available
        authToken?.let { token ->
            requestBuilder.header("Authorization", "Bearer $token")
        }
        
        return chain.proceed(requestBuilder.build())
    }
    
    /**
     * Set the authentication token.
     * Call this after user logs in.
     */
    fun setAuthToken(token: String?) {
        this.authToken = token
    }
    
    /**
     * Clear the authentication token.
     * Call this when user logs out.
     */
    fun clearAuthToken() {
        this.authToken = null
    }
}
