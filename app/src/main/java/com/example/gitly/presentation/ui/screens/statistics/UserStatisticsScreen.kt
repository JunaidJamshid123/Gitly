package com.example.gitly.presentation.ui.screens.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.gitly.data.api.RetrofitClient
import com.example.gitly.presentation.viewmodel.StatisticsViewModel
import com.example.gitly.presentation.viewmodel.UserStatsState
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserStatisticsScreen(navController: NavHostController, username: String) {
    val viewModel = remember { StatisticsViewModel(RetrofitClient.apiService) }
    val statsState by viewModel.userStatsState.collectAsState()

    LaunchedEffect(username) {
        viewModel.fetchUserStatistics(username)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics", fontSize = 18.sp, fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        when (val state = statsState) {
            is UserStatsState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }
            is UserStatsState.Success -> {
                UserStatisticsContent(
                    statistics = state.statistics,
                    modifier = Modifier.padding(padding)
                )
            }
            is UserStatsState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun UserStatisticsContent(
    statistics: com.example.gitly.data.model.UserStatistics,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // User Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = statistics.avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = statistics.name ?: statistics.username,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "@${statistics.username}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "On GitHub for ${statistics.accountAge}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Overview Stats
        Text(
            text = "Overview",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Repositories",
                value = statistics.totalRepos.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Total Stars",
                value = formatNumber(statistics.totalStars),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Followers",
                value = formatNumber(statistics.followers),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Following",
                value = formatNumber(statistics.following),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Language Statistics
        if (statistics.languageStats.isNotEmpty()) {
            Text(
                text = "Languages",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Language Distribution Bar Chart
                    PieChartWithLabels(
                        data = statistics.languageStats,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Top Repositories
        if (statistics.topRepositories.isNotEmpty()) {
            Text(
                text = "Top Repositories",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            statistics.topRepositories.forEach { repo ->
                TopRepoCard(repo = repo)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun LanguageStatRow(language: String, count: Int, total: Int) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(getLanguageColor(language))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = language,
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "$count repos",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { count.toFloat() / total.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = getLanguageColor(language),
            trackColor = Color(0xFFE5E7EB)
        )
    }
}

@Composable
fun TopRepoCard(repo: com.example.gitly.data.model.TopRepository) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = repo.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (repo.language != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(getLanguageColor(repo.language))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = repo.language,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            Row {
                Text(
                    text = "⭐ ${formatNumber(repo.stars)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "🔀 ${formatNumber(repo.forks)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

fun getLanguageColor(language: String): Color {
    return when (language.lowercase()) {
        "kotlin" -> Color(0xFFA97BFF)
        "java" -> Color(0xFFB07219)
        "javascript" -> Color(0xFFF1E05A)
        "typescript" -> Color(0xFF2B7489)
        "python" -> Color(0xFF3572A5)
        "swift" -> Color(0xFFFFAC45)
        "go" -> Color(0xFF00ADD8)
        "rust" -> Color(0xFFDEA584)
        "c++" -> Color(0xFFF34B7D)
        "c" -> Color(0xFF555555)
        "c#" -> Color(0xFF178600)
        "ruby" -> Color(0xFF701516)
        "php" -> Color(0xFF4F5D95)
        "css" -> Color(0xFF563D7C)
        "html" -> Color(0xFFE34C26)
        "dart" -> Color(0xFF00B4AB)
        "shell" -> Color(0xFF89E051)
        "objective-c" -> Color(0xFF438EFF)
        "scala" -> Color(0xFFC22D40)
        "r" -> Color(0xFF198CE7)
        else -> Color(0xFF6B7280)
    }
}

fun formatNumber(number: Int): String {
    return when {
        number >= 1000000 -> String.format("%.1fM", number / 1000000.0)
        number >= 1000 -> String.format("%.1fK", number / 1000.0)
        else -> number.toString()
    }
}


@Composable
fun PieChartWithLabels(
    data: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    val sortedData = remember(data) {
        data.entries
            .sortedByDescending { it.value }
            .take(5) // Show top 5 languages
    }
    
    val total = remember(sortedData) {
        sortedData.sumOf { it.value }.toFloat()
    }
    
    var hoveredLanguage by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Donut Chart with labels
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            // Calculate which segment was tapped
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f
                            val dx = offset.x - centerX
                            val dy = offset.y - centerY
                            val distance = sqrt(dx * dx + dy * dy)
                            
                            val canvasSize = minOf(size.width, size.height)
                            val radius = canvasSize / 2.8f
                            val strokeWidth = radius * 0.35f
                            val innerRadius = radius - strokeWidth
                            val outerRadius = radius
                            
                            // Check if tap is within the donut ring
                            if (distance >= innerRadius && distance <= outerRadius) {
                                var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                angle = (angle + 90 + 360) % 360
                                
                                var startAngle = 0f
                                sortedData.forEach { (language, count) ->
                                    val sweepAngle = (count / total) * 360f
                                    if (angle >= startAngle && angle < startAngle + sweepAngle) {
                                        hoveredLanguage = language
                                        return@detectTapGestures
                                    }
                                    startAngle += sweepAngle
                                }
                            } else {
                                hoveredLanguage = null
                            }
                        }
                    }
            ) {
                val canvasSize = min(size.width, size.height)
                val radius = canvasSize / 2.8f
                val strokeWidth = radius * 0.45f  // Thicker segments for better visibility
                val centerOffset = Offset(size.width / 2f, size.height / 2f)
                
                var startAngle = -90f
                
                sortedData.forEach { (language, count) ->
                    val sweepAngle = (count / total) * 360f
                    val percentage = (count / total) * 100f
                    val color = getLanguageColor(language)
                    
                    // Draw donut segment
                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(
                            centerOffset.x - radius,
                            centerOffset.y - radius
                        ),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth)
                    )
                    
                    // Calculate label position outside the chart
                    val middleAngle = startAngle + sweepAngle / 2
                    val angleInRadians = Math.toRadians(middleAngle.toDouble())
                    val labelRadius = radius + strokeWidth / 2 + 60f  // Moved further out
                    
                    val labelX = centerOffset.x + labelRadius * cos(angleInRadians).toFloat()
                    val labelY = centerOffset.y + labelRadius * sin(angleInRadians).toFloat()
                    
                    // Calculate connection point on the arc
                    val arcRadius = radius - strokeWidth / 2
                    val arcX = centerOffset.x + arcRadius * cos(angleInRadians).toFloat()
                    val arcY = centerOffset.y + arcRadius * sin(angleInRadians).toFloat()
                    
                    // Draw connecting line for all segments
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.6f),
                        start = Offset(arcX, arcY),
                        end = Offset(labelX, labelY),
                        strokeWidth = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f))
                    )
                    
                    // Draw percentage text with background for better visibility
                    drawContext.canvas.nativeCanvas.apply {
                        val text = String.format("%.1f%%", percentage)
                        
                        // Background paint
                        val bgPaint = android.graphics.Paint().apply {
                            setColor(android.graphics.Color.argb(230, 255, 255, 255))
                            style = android.graphics.Paint.Style.FILL
                        }
                        
                        // Text paint
                        val textPaint = android.graphics.Paint().apply {
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 30f  // Larger text
                            setColor(android.graphics.Color.BLACK)
                            isFakeBoldText = true
                        }
                        
                        val textBounds = android.graphics.Rect()
                        textPaint.getTextBounds(text, 0, text.length, textBounds)
                        
                        // Draw rounded background
                        drawRoundRect(
                            labelX - textBounds.width() / 2 - 10f,
                            labelY - textBounds.height() / 2 - 8f,
                            labelX + textBounds.width() / 2 + 10f,
                            labelY + textBounds.height() / 2 + 8f,
                            12f,
                            12f,
                            bgPaint
                        )
                        
                        // Draw text
                        drawText(
                            text,
                            labelX,
                            labelY + textBounds.height() / 2,
                            textPaint
                        )
                    }
                    
                    startAngle += sweepAngle
                }
                
                // Draw white center circle for donut effect
                drawCircle(
                    color = Color.White,
                    radius = radius - strokeWidth,
                    center = centerOffset
                )
            }
            
            // Show tooltip on hover/tap
            hoveredLanguage?.let { language ->
                val count = data[language] ?: 0
                val percentage = (count / total) * 100f
                
                Surface(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xF0FFFFFF),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(getLanguageColor(language))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = language,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$count repos (${String.format("%.1f%%", percentage)})",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Legend below the chart
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            sortedData.forEach { (language, count) ->
                val percentage = (count / total) * 100f
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(getLanguageColor(language))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = language,
                            fontSize = 14.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Text(
                        text = String.format("%.1f%% • %d repos", percentage, count),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}


