package com.example.gitly.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gitly.R

@Composable
fun AnimatedLoadingScreen() {
    // Infinite rotation animation
    var rotation by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(16) // ~60 FPS
            rotation = (rotation + 3f) % 360f
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated rotating image
            Image(
                painter = painterResource(id = R.drawable.loading_gif),
                contentDescription = "Loading",
                modifier = Modifier
                    .size(120.dp)
                    .rotate(rotation),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Loading text
            Text(
                text = "Loading...",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF6366F1),
                letterSpacing = 0.5.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Fetching data",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF9CA3AF),
                letterSpacing = 0.3.sp
            )
        }
    }
}
