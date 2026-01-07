package com.example.gitly.presentation.ui.screens.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gitly.data.model.GitHubRepo
import com.example.gitly.data.model.GitHubUser
import com.example.gitly.presentation.navigation.BottomNavItem
import com.example.gitly.presentation.navigation.Routes
import kotlin.math.min
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(navController: NavHostController) {
    val viewModel: HomeScreenViewModel = viewModel()
    val homeState by viewModel.homeState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        if (homeState.isLoading) {
            // Loading State
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF64B5F6))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                // 1. Hero Section
                item {
                    HeroSection(userName = homeState.userName)
                }

                // 2. GitHub Fun Fact
                item {
                    GitHubFunFactCard()
                }

                // 3. Popular Languages (Static)
                item {
                    PopularLanguagesSection()
                }

                // 4. Trending Repositories
                if (homeState.trendingRepos.isNotEmpty()) {
                    item {
                        TrendingReposSection(
                            repos = homeState.trendingRepos,
                            navController = navController
                        )
                    }
                }

                // 5. Trending Users
                if (homeState.trendingUsers.isNotEmpty()) {
                    item {
                        TrendingUsersSection(
                            users = homeState.trendingUsers,
                            navController = navController
                        )
                    }
                }

                // 6. Explore by Language
                item {
                    ExploreByLanguageSection(navController)
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun HeroSection(userName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "👋 Welcome Back,",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6B7280),
                letterSpacing = 0.3.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = userName,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Discover the world of open source",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF9CA3AF),
                lineHeight = 20.sp,
                letterSpacing = 0.2.sp
            )
        }
    }
}

@Composable
fun GitHubFunFactCard() {
    val facts = listOf(
        "GitHub has over 100 million developers worldwide",
        "The most popular programming language on GitHub is JavaScript",
        "Over 300 million pull requests are created annually",
        "GitHub was founded in 2008 and acquired by Microsoft in 2018",
        "The largest repository has over 1 million stars",
        "Developers push over 1 billion commits to GitHub each year"
    )
    
    var currentFactIndex by remember { mutableStateOf(0) }
    var targetFactIndex by remember { mutableStateOf(0) }
    
    // Auto-rotate facts every 10 seconds
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(10000) // 10 seconds
            targetFactIndex = (targetFactIndex + 1) % facts.size
        }
    }
    
    // Animate the transition
    val animatedProgress by animateFloatAsState(
        targetValue = if (currentFactIndex == targetFactIndex) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "fact_transition"
    )
    
    LaunchedEffect(targetFactIndex) {
        if (currentFactIndex != targetFactIndex) {
            kotlinx.coroutines.delay(300)
            currentFactIndex = targetFactIndex
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount > 50) {
                        // Swipe right - previous fact
                        targetFactIndex = if (targetFactIndex == 0) facts.size - 1 else targetFactIndex - 1
                    } else if (dragAmount < -50) {
                        // Swipe left - next fact
                        targetFactIndex = (targetFactIndex + 1) % facts.size
                    }
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3F4F6))
                    .graphicsLayer {
                        rotationZ = (1f - animatedProgress) * 360f
                        alpha = animatedProgress
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💡",
                    fontSize = 22.sp
                )
            }
            
            Spacer(modifier = Modifier.width(14.dp))
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer {
                        alpha = animatedProgress
                        translationX = (1f - animatedProgress) * 20f
                    }
            ) {
                Text(
                    text = "Did you know?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6366F1),
                    letterSpacing = 0.3.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = facts[currentFactIndex],
                    fontSize = 14.sp,
                    color = Color(0xFF374151),
                    lineHeight = 20.sp,
                    letterSpacing = 0.1.sp
                )
            }
        }
    }
}@Composable
fun PopularLanguagesSection() {
    val staticLanguages = listOf(
        LanguageData("JavaScript", 21.0f, "#F7DF1E"),
        LanguageData("Java", 18.0f, "#B07219"),
        LanguageData("Python", 15.5f, "#3776AB"),
        LanguageData("TypeScript", 15.0f, "#3178C6"),
        LanguageData("C++", 12.5f, "#F34B7D"),
        LanguageData("Go", 7.0f, "#00ADD8"),
        LanguageData("Kotlin", 5.0f, "#F18E33"),
        LanguageData("Swift", 3.5f, "#FA7343"),
        LanguageData("Rust", 2.5f, "#CE422B")
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Popular Languages",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                letterSpacing = (-0.3).sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Pie Chart
            StaticLanguagePieChart(
                languages = staticLanguages,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Legend
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                staticLanguages.forEach { lang ->
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
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(android.graphics.Color.parseColor(lang.color).let { Color(it) })
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = lang.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1F2937)
                            )
                        }

                        Text(
                            text = "${lang.percentage}%",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }
        }
    }
}

data class LanguageData(
    val name: String,
    val percentage: Float,
    val color: String
)

@Composable
fun StaticLanguagePieChart(
    languages: List<LanguageData>,
    modifier: Modifier = Modifier
) {
    // Animate the chart on first appearance
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "chart_animation"
    )
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = 0.5f + (animatedProgress * 0.5f)
                    scaleY = 0.5f + (animatedProgress * 0.5f)
                    alpha = animatedProgress
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val radius = min(canvasWidth, canvasHeight) / 2.5f
            val centerX = canvasWidth / 2f
            val centerY = canvasHeight / 2f

            var startAngle = -90f

            languages.forEach { lang ->
                val sweepAngle = (lang.percentage / 100f) * 360f * animatedProgress
                val color = android.graphics.Color.parseColor(lang.color)

                // Draw pie slice
                drawArc(
                    color = Color(color),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(centerX - radius, centerY - radius),
                    size = Size(radius * 2, radius * 2)
                )

                // Draw label on each section (show for all sections >= 2.5%)
                if (lang.percentage >= 2.5f && animatedProgress > 0.8f) {
                    val middleAngle = startAngle + sweepAngle / 2
                    val angleInRadians = Math.toRadians(middleAngle.toDouble())
                    val labelRadius = radius * 0.7f

                    val labelX = centerX + labelRadius * kotlin.math.cos(angleInRadians).toFloat()
                    val labelY = centerY + labelRadius * kotlin.math.sin(angleInRadians).toFloat()

                    // Draw percentage text
                    drawContext.canvas.nativeCanvas.apply {
                        val textPaint = android.graphics.Paint().apply {
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = if (lang.percentage >= 10f) 32f else 26f
                            setColor(android.graphics.Color.WHITE)
                            isFakeBoldText = true
                            setShadowLayer(4f, 0f, 2f, android.graphics.Color.argb(140, 0, 0, 0))
                            alpha = ((animatedProgress - 0.8f) / 0.2f * 255).toInt().coerceIn(0, 255)
                        }

                        drawText(
                            "${lang.percentage}%",
                            labelX,
                            labelY + 10f,
                            textPaint
                        )
                    }
                }

                startAngle += sweepAngle
            }

            // Draw white center circle for donut effect
            drawCircle(
                color = Color.White,
                radius = radius * 0.5f,
                center = Offset(centerX, centerY)
            )
        }
    }
}

@Composable
fun TrendingReposSection(repos: List<GitHubRepo>, navController: NavHostController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Trending Repositories",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                letterSpacing = (-0.3).sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(repos.take(7)) { repo ->
                TrendingRepoCard(repo = repo, navController = navController)
            }
        }
    }
}

@Composable
fun TrendingRepoCard(repo: GitHubRepo, navController: NavHostController) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(140.dp)
            .clickable {
                navController.navigate(
                    Routes.repoDetail(
                        repo.owner.login,
                        repo.name
                    )
                )
            },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Owner and Repo Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(repo.owner.avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Owner Avatar",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${repo.owner.login}/${repo.name}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF64B5F6),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = repo.description ?: "No description available",
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Language
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (repo.language != null) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(getLanguageColor(repo.language))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = repo.language,
                            fontSize = 11.sp,
                            color = Color.Black
                        )
                    }
                }

                // Stars
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Stars",
                        tint = Color(0xFFFFA726),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatNumber(repo.stargazersCount ?: 0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun TrendingUsersSection(users: List<GitHubUser>, navController: NavHostController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Top Developers",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                letterSpacing = (-0.3).sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            users.take(3).forEach { user ->
                TrendingUserCard(user = user, navController = navController)
            }
        }
    }
}

@Composable
fun TrendingUserCard(user: GitHubUser, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(Routes.userDetail(user.login))
            },
        shape = RoundedCornerShape(14.dp),
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
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.avatar_url)
                    .crossfade(true)
                    .build(),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name ?: user.login,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    letterSpacing = 0.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "@${user.login}",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = formatNumber(user.followers ?: 0),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    letterSpacing = (-0.2).sp
                )
                Text(
                    text = "followers",
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF),
                    letterSpacing = 0.2.sp
                )
            }
        }
    }
}

@Composable
fun ExploreByLanguageSection(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Explore by Language",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                letterSpacing = (-0.3).sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid layout instead of horizontal scroll
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // First row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LanguageChip(
                    language = "Kotlin",
                    color = Color(0xFFA97BFF),
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
                LanguageChip(
                    language = "JavaScript",
                    color = Color(0xFFF1E05A),
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
                LanguageChip(
                    language = "Python",
                    color = Color(0xFF3572A5),
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            }

            // Second row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LanguageChip(
                    language = "TypeScript",
                    color = Color(0xFF2B7489),
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
                LanguageChip(
                    language = "Java",
                    color = Color(0xFFB07219),
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
                LanguageChip(
                    language = "Go",
                    color = Color(0xFF00ADD8),
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            }

            // Third row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LanguageChip(
                    language = "Rust",
                    color = Color(0xFFDEA584),
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
                LanguageChip(
                    language = "Swift",
                    color = Color(0xFFFFAC45),
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
                LanguageChip(
                    language = "C++",
                    color = Color(0xFFF34B7D),
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun LanguageChip(
    language: String,
    color: Color,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = language,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// Helper Functions
fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> String.format(Locale.US, "%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format(Locale.US, "%.1fK", number / 1_000.0)
        else -> NumberFormat.getNumberInstance(Locale.US).format(number)
    }
}

fun formatRepoCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format(Locale.US, "%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format(Locale.US, "%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}

fun Float.format(decimals: Int): String {
    return "%.${decimals}f".format(this)
}

fun getLanguageColor(language: String): Color {
    return when (language.lowercase()) {
        "kotlin" -> Color(0xFF9B7EDE)
        "java" -> Color(0xFFB07219)
        "javascript" -> Color(0xFFF1E05A)
        "typescript" -> Color(0xFF2B7489)
        "python" -> Color(0xFF3572A5)
        "html" -> Color(0xFFE34C26)
        "css" -> Color(0xFF563D7C)
        "c++" -> Color(0xFFEF5FA7)
        "c" -> Color(0xFF555555)
        "go" -> Color(0xFF00ADD8)
        "rust" -> Color(0xFFDEA584)
        "swift" -> Color(0xFFFFAC45)
        "php" -> Color(0xFF4F5D95)
        "ruby" -> Color(0xFF701516)
        "dart" -> Color(0xFF00B4AB)
        else -> Color(0xFF666666)
    }
}