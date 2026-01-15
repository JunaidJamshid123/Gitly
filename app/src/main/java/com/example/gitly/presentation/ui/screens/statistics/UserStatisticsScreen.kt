package com.example.gitly.presentation.ui.screens.statistics

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.graphics.vector.ImageVector
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

private val IndigoColor = Color(0xFF6366F1)
private val BorderColor = Color(0xFFE5E7EB)

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
                title = { 
                    Text(
                        "📊 User Statistics", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.SemiBold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, "Back")
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
                    CircularProgressIndicator(color = IndigoColor)
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(state.message, color = Color(0xFFEF4444), fontSize = 14.sp)
                    }
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
    // Animation state for staggered entrance
    var animationPlayed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animationPlayed = true }
    
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
        label = "content_animation"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // User Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(IndigoColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = statistics.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = statistics.name ?: statistics.username,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = "@${statistics.username}",
                        fontSize = 14.sp,
                        color = IndigoColor,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(
                            Icons.Outlined.AccessTime,
                            contentDescription = null,
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "On GitHub for ${statistics.accountAge}",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Overview Stats Section
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Text(text = "📈", fontSize = 18.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Overview",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserStatCard(
                title = "Repositories",
                value = statistics.totalRepos.toString(),
                icon = Icons.Outlined.Folder,
                iconColor = IndigoColor,
                animatedProgress = animatedProgress,
                modifier = Modifier.weight(1f)
            )
            UserStatCard(
                title = "Total Stars",
                value = formatNumber(statistics.totalStars),
                icon = Icons.Outlined.StarOutline,
                iconColor = Color(0xFFF59E0B),
                animatedProgress = animatedProgress,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserStatCard(
                title = "Followers",
                value = formatNumber(statistics.followers),
                icon = Icons.Outlined.People,
                iconColor = Color(0xFF10B981),
                animatedProgress = animatedProgress,
                modifier = Modifier.weight(1f)
            )
            UserStatCard(
                title = "Following",
                value = formatNumber(statistics.following),
                icon = Icons.Outlined.PersonAdd,
                iconColor = Color(0xFF8B5CF6),
                animatedProgress = animatedProgress,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Language Statistics
        if (statistics.languageStats.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(text = "💻", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Languages Used",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Language Distribution Bar Chart
                    AnimatedPieChartWithLabels(
                        data = statistics.languageStats,
                        animatedProgress = animatedProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Top Repositories
        if (statistics.topRepositories.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(text = "🏆", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Top Repositories",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }

            statistics.topRepositories.forEachIndexed { index, repo ->
                TopRepoCard(repo = repo, rank = index + 1)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun UserStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    animatedProgress: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
fun LanguageStatRow(language: String, count: Int, total: Int, animatedProgress: Float) {
    val animatedWidth by animateFloatAsState(
        targetValue = (count.toFloat() / total.toFloat()) * animatedProgress,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 100f),
        label = "language_progress"
    )
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(getLanguageColor(language))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = language,
                    fontSize = 14.sp,
                    color = Color(0xFF1F2937),
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "$count repos",
                fontSize = 12.sp,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(BorderColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedWidth)
                    .clip(RoundedCornerShape(4.dp))
                    .background(getLanguageColor(language))
            )
        }
    }
}

@Composable
fun TopRepoCard(repo: com.example.gitly.data.model.TopRepository, rank: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Badge
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when (rank) {
                            1 -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                            2 -> Color(0xFF6B7280).copy(alpha = 0.15f)
                            3 -> Color(0xFFF97316).copy(alpha = 0.15f)
                            else -> IndigoColor.copy(alpha = 0.1f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (rank) {
                        1 -> "🥇"
                        2 -> "🥈"
                        3 -> "🥉"
                        else -> "#$rank"
                    },
                    fontSize = if (rank <= 3) 16.sp else 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = IndigoColor
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = repo.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )
                if (repo.language != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(getLanguageColor(repo.language))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = repo.language,
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.StarOutline,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatNumber(repo.stars),
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.Medium
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.AccountTree,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatNumber(repo.forks),
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.Medium
                    )
                }
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
fun AnimatedPieChartWithLabels(
    data: Map<String, Int>,
    animatedProgress: Float,
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
    
    // Animate the sweep
    val animatedSweep by animateFloatAsState(
        targetValue = 360f * animatedProgress,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 100f),
        label = "pie_sweep"
    )
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Donut Chart with labels
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
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
                val strokeWidth = radius * 0.5f  // Thicker segments for better visibility
                val centerOffset = Offset(size.width / 2f, size.height / 2f)
                
                var startAngle = -90f
                var drawnAngle = 0f
                
                sortedData.forEach { (language, count) ->
                    val sweepAngle = (count / total) * 360f
                    val color = getLanguageColor(language)
                    
                    // Only draw if we haven't exceeded the animated sweep
                    if (drawnAngle < animatedSweep) {
                        val actualSweep = minOf(sweepAngle, animatedSweep - drawnAngle)
                        
                        // Draw donut segment
                        drawArc(
                            color = color,
                            startAngle = startAngle,
                            sweepAngle = actualSweep,
                            useCenter = false,
                            topLeft = Offset(
                                centerOffset.x - radius,
                                centerOffset.y - radius
                            ),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth)
                        )
                    }
                    
                    drawnAngle += sweepAngle
                    startAngle += sweepAngle
                }
                
                // Draw white center circle for donut effect
                drawCircle(
                    color = Color.White,
                    radius = radius - strokeWidth,
                    center = centerOffset
                )
            }
            
            // Center text showing total
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = sortedData.size.toString(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = "Languages",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
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
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(getLanguageColor(language))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = language,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$count repos (${String.format("%.1f%%", percentage)})",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Legend below the chart
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                                .size(18.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(getLanguageColor(language))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = language,
                            fontSize = 14.sp,
                            color = Color(0xFF1F2937),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Text(
                        text = String.format("%.1f%% • %d repos", percentage, count),
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}


