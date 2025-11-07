package com.example.gitly.presentation.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Star
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
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
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
                
                // 2. Quick Action Buttons
                item {
                    QuickActionButtons(navController)
                }
                
                // 3. GitHub Global Statistics
                if (homeState.topLanguages.isNotEmpty() || homeState.isLoadingLanguages) {
                    item {
                        GitHubGlobalStatsSection(
                            languages = homeState.topLanguages,
                            homeState = homeState
                        )
                    }
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "👋 Welcome Back,",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = userName,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Discover the world of open source",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun QuickActionButtons(navController: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search Users Button
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { navController.navigate(BottomNavItem.UserDetails.route) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Users",
                    tint = Color(0xFF64B5F6),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Search Users",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }
        
        // Browse Repos Button
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { navController.navigate(BottomNavItem.RepoDetails.route) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Repos",
                    tint = Color(0xFF64B5F6),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Browse Repos",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun GitHubFactCard(fact: String, onRefresh: () -> Unit) {
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
            // Icon in a circular background
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3F4F6)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💡",
                    fontSize = 20.sp
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Did you know?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64B5F6)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = fact,
                    fontSize = 13.sp,
                    color = Color.Black,
                    lineHeight = 18.sp
                )
            }
            
            IconButton(
                onClick = onRefresh,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = Color(0xFF64B5F6),
                    modifier = Modifier.size(20.dp)
                )
            }
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
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
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
                    com.example.gitly.presentation.navigation.NavRoutes.repoDetails(
                        repo.owner.login,
                        repo.name
                    )
                )
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
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
                text = "Trending Developers",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
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
                navController.navigate("user_profile/${user.login}")
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.avatar_url)
                    .crossfade(true)
                    .build(),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name ?: user.login,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "@${user.login}",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (user.bio != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = user.bio,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatNumber(user.followers ?: 0),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "followers",
                    fontSize = 11.sp,
                    color = Color.Gray
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
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
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
        modifier = modifier.clickable {
            navController.navigate(BottomNavItem.RepoDetails.route)
        },
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

@Composable
fun GitHubGlobalStatsSection(languages: List<LanguageStat>, homeState: HomeScreenState) {
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
            // Section Header
            Text(
                text = "Popular Languages",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Pie Chart
            LanguagePieChart(
                languages = languages.take(9), // Show all 9 languages
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)  // Increased height for better visibility
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Legend - Simple list without rank badges
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (homeState.isLoadingLanguages) {
                    // Show loading indicator
                    repeat(5) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0E0E0))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(
                                modifier = Modifier
                                    .height(16.dp)
                                    .width(120.dp)
                                    .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
                            )
                        }
                    }
                } else {
                    languages.take(9).forEach { lang ->
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
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(getLanguageColor(lang.name))
                                )
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Text(
                                    text = lang.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.Black
                                )
                            }
                            
                            Text(
                                text = if (lang.repoCount > 0) {
                                    "${lang.percentage.format(1)}% • ${formatRepoCount(lang.repoCount)} repos"
                                } else {
                                    "${lang.percentage.format(1)}%"
                                },
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun LanguagePieChart(
    languages: List<LanguageStat>,
    modifier: Modifier = Modifier
) {
    var hoveredLanguage by remember { mutableStateOf<String?>(null) }
    
    val total = remember(languages) {
        languages.sumOf { it.percentage.toDouble() }.toFloat()
    }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val dx = offset.x - centerX
                        val dy = offset.y - centerY
                        val distance = sqrt(dx * dx + dy * dy)
                        
                        val canvasSize = minOf(size.width, size.height)
                        val radius = canvasSize / 2.5f
                        val strokeWidth = radius * 0.4f
                        val innerRadius = radius - strokeWidth
                        val outerRadius = radius
                        
                        if (distance >= innerRadius && distance <= outerRadius) {
                            var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                            angle = (angle + 90 + 360) % 360
                            
                            var startAngle = 0f
                            languages.forEach { lang ->
                                val sweepAngle = (lang.percentage / total) * 360f
                                if (angle >= startAngle && angle < startAngle + sweepAngle) {
                                    hoveredLanguage = lang.name
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
            val radius = canvasSize / 3.0f  // Larger radius for better visibility
            val strokeWidth = radius * 0.65f  // Even thicker donut ring (65%)
            val centerOffset = Offset(size.width / 2f, size.height / 2f)
            
            var startAngle = -90f
            
            languages.forEach { lang ->
                val sweepAngle = (lang.percentage / total) * 360f
                val percentage = lang.percentage
                val color = getLanguageColor(lang.name)
                
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
                
                // Only draw percentage for segments >= 2%
                if (percentage >= 2f) {
                    val middleAngle = startAngle + sweepAngle / 2
                    val angleInRadians = Math.toRadians(middleAngle.toDouble())
                    val textRadius = radius - strokeWidth / 2
                    
                    val textX = centerOffset.x + textRadius * cos(angleInRadians).toFloat()
                    val textY = centerOffset.y + textRadius * sin(angleInRadians).toFloat()
                    
                    // Draw percentage text - larger and more visible
                    drawContext.canvas.nativeCanvas.apply {
                        val textPaint = android.graphics.Paint().apply {
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = if (percentage >= 10f) 38f else 32f  // Larger text
                            setColor(android.graphics.Color.WHITE)
                            isFakeBoldText = true
                            setShadowLayer(4f, 0f, 2f, android.graphics.Color.argb(150, 0, 0, 0))
                        }
                        
                        drawText(
                            String.format("%.1f%%", percentage),
                            textX,
                            textY + 12f,
                            textPaint
                        )
                    }
                }
                
                startAngle += sweepAngle
            }
            
            // Draw white center circle
            drawCircle(
                color = Color.White,
                radius = radius - strokeWidth,
                center = centerOffset
            )
        }
        
        // Tooltip on hover
        hoveredLanguage?.let { langName ->
            val lang = languages.find { it.name == langName }
            lang?.let {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xF0FFFFFF),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(getLanguageColor(it.name))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${it.name} - ${it.percentage}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
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
        "kotlin" -> Color(0xFF9B7EDE)  // Purple to match screenshot
        "java" -> Color(0xFFB07219)
        "javascript" -> Color(0xFFF1E05A)
        "typescript" -> Color(0xFF2B7489)
        "python" -> Color(0xFF3572A5)
        "html" -> Color(0xFFE34C26)
        "css" -> Color(0xFF563D7C)
        "c++" -> Color(0xFFEF5FA7)  // Pink for C++
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