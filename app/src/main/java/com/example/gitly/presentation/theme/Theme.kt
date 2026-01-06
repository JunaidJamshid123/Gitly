package com.example.gitly.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Blue400,
    onPrimary = Gray900,
    primaryContainer = Blue700,
    onPrimaryContainer = Blue100,
    secondary = Gray600,
    onSecondary = Gray100,
    secondaryContainer = Gray800,
    onSecondaryContainer = Gray200,
    tertiary = Green400,
    onTertiary = Gray900,
    tertiaryContainer = Green500,
    onTertiaryContainer = Gray100,
    error = Red400,
    onError = Gray900,
    errorContainer = Red500,
    onErrorContainer = Gray100,
    background = GithubDark,
    onBackground = GithubText,
    surface = GithubDarkSecondary,
    onSurface = GithubText,
    surfaceVariant = GithubDarkTertiary,
    onSurfaceVariant = GithubTextSecondary,
    outline = GithubBorder,
    outlineVariant = GithubBorder,
    scrim = Gray900,
    inverseSurface = Gray100,
    inverseOnSurface = Gray900,
    inversePrimary = Blue700
)

private val LightColorScheme = lightColorScheme(
    primary = Blue500,
    onPrimary = Gray50,
    primaryContainer = Blue100,
    onPrimaryContainer = Blue900,
    secondary = Gray500,
    onSecondary = Gray50,
    secondaryContainer = Gray200,
    onSecondaryContainer = Gray800,
    tertiary = Green500,
    onTertiary = Gray50,
    tertiaryContainer = Green400,
    onTertiaryContainer = Gray900,
    error = Red500,
    onError = Gray50,
    errorContainer = Red400,
    onErrorContainer = Gray900,
    background = Gray50,
    onBackground = Gray900,
    surface = Gray50,
    onSurface = Gray900,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray600,
    outline = Gray300,
    outlineVariant = Gray200,
    scrim = Gray900,
    inverseSurface = Gray800,
    inverseOnSurface = Gray100,
    inversePrimary = Blue300
)

@Composable
fun GitlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

/**
 * Utility function to get language color by name.
 */
fun getLanguageColor(language: String?): androidx.compose.ui.graphics.Color {
    return when (language?.lowercase()) {
        "kotlin" -> LanguageKotlin
        "java" -> LanguageJava
        "javascript" -> LanguageJavaScript
        "typescript" -> LanguageTypeScript
        "python" -> LanguagePython
        "go", "golang" -> LanguageGo
        "rust" -> LanguageRust
        "swift" -> LanguageSwift
        "c" -> LanguageC
        "c++" -> LanguageCpp
        "c#" -> LanguageCSharp
        "ruby" -> LanguageRuby
        "dart" -> LanguageDart
        else -> LanguageDefault
    }
}