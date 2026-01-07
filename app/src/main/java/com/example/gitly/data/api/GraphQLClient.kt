package com.example.gitly.data.api

import com.example.gitly.BuildConfig
import com.example.gitly.data.model.ContributionCalendarResponse
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface GitHubGraphQLService {
    @POST("graphql")
    suspend fun query(@Body query: JsonObject): ContributionCalendarResponse
}

object GraphQLClient {
    private const val BASE_URL = "https://api.github.com/"
    private val GITHUB_TOKEN = BuildConfig.GITHUB_TOKEN
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val authInterceptor = okhttp3.Interceptor { chain ->
        val originalRequest = chain.request()
        val requestWithAuth = originalRequest.newBuilder()
            .header("Authorization", "Bearer $GITHUB_TOKEN")
            .header("Content-Type", "application/json")
            .build()
        chain.proceed(requestWithAuth)
    }
    
    private val cacheInterceptor = okhttp3.Interceptor { chain ->
        val request = chain.request()
        val response = chain.proceed(request)
        
        // Cache responses for 1 hour (contributions don't change frequently)
        val cacheControl = okhttp3.CacheControl.Builder()
            .maxAge(1, TimeUnit.HOURS)
            .build()
        
        response.newBuilder()
            .header("Cache-Control", cacheControl.toString())
            .removeHeader("Pragma")
            .build()
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(cacheInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: GitHubGraphQLService = retrofit.create(GitHubGraphQLService::class.java)
    
    // Helper function to build contribution calendar query
    fun buildContributionQuery(username: String): JsonObject {
        val query = """
            query {
              user(login: "$username") {
                contributionsCollection {
                  contributionCalendar {
                    totalContributions
                    weeks {
                      contributionDays {
                        date
                        contributionCount
                        weekday
                        color
                      }
                    }
                  }
                }
              }
            }
        """.trimIndent()
        
        return JsonObject().apply {
            addProperty("query", query)
        }
    }
}
