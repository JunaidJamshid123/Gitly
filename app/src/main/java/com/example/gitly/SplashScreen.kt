package com.example.gitly

import androidx.compose.runtime.Composable

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gitly.R
import kotlinx.coroutines.delay
import androidx.navigation.NavController
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(onNavigateToHome: () -> Unit) {
    // Animation for scale
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Fade in animation
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    LaunchedEffect(key1 = true) {
        delay(3000) // 3-second splash delay
        onNavigateToHome()
    }

    // Gradient background
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE3F2FD), // Light blue
            Color(0xFFFFFFFF), // White
            Color(0xFFF5F5F5)  // Light gray
        )
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
    ){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBrush)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ){
            // Animated GitHub icon
            Image(
                painter = painterResource(R.drawable.github),
                contentDescription = "github image",
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale)
                    .alpha(alpha),

            )
            Spacer(modifier = Modifier.height(32.dp))

            // App name with animation
            Text(
                text = "Gitly",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1976D2), // Material blue
                    textAlign = TextAlign.Center,
                    fontSize = 42.sp,
                    letterSpacing = 2.sp
                ),
                modifier = Modifier.alpha(alpha)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Tagline
            Text(
                text = "Explore GitHub Like Never Before",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF616161), // Dark gray
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp
                ),
                modifier = Modifier
                    .alpha(alpha)
                    .padding(horizontal = 32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtitle
            Text(
                text = "Discover • Connect • Collaborate",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF9E9E9E), // Light gray
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.alpha(alpha * 0.8f)
            )
        }
    }
}

