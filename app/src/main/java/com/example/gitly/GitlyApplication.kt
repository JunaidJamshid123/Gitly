package com.example.gitly

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Gitly.
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class GitlyApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize any app-wide configurations here
    }
}
