package com.example.gitly.presentation.ui.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.example.gitly.presentation.ui.components.AnimatedLoadingScreen
import com.example.gitly.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
            // Custom Animated Loading State
            AnimatedLoadingScreen()
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
    // Subtle entrance animation
    var visible by remember { mutableStateOf(false) }
    val animatedAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600),
        label = "hero_alpha"
    )
    val animatedOffset by animateFloatAsState(
        targetValue = if (visible) 0f else 20f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "hero_offset"
    )
    
    LaunchedEffect(Unit) { visible = true }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = animatedAlpha
                translationY = animatedOffset
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEEF2FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👋", fontSize = 26.sp)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Welcome back,",
                        fontSize = 13.sp,
                        color = Color(0xFF9CA3AF)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = userName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Discover trending repos, top developers & open source insights",
                fontSize = 13.sp,
                color = Color(0xFF6B7280),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun GitHubFunFactCard() {
    val facts = listOf(
        "GitHub has over 100 million developers worldwide",
        "JavaScript is the most popular language on GitHub",
        "Over 300 million pull requests created annually",
        "GitHub was founded in 2008",
        "1 billion+ commits pushed to GitHub each year"
    )
    
    var currentFactIndex by remember { mutableStateOf(0) }
    var targetFactIndex by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(8000)
            targetFactIndex = (targetFactIndex + 1) % facts.size
        }
    }
    
    val animatedProgress by animateFloatAsState(
        targetValue = if (currentFactIndex == targetFactIndex) 1f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "fact_transition"
    )
    
    LaunchedEffect(targetFactIndex) {
        if (currentFactIndex != targetFactIndex) {
            delay(250)
            currentFactIndex = targetFactIndex
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount > 50) {
                        targetFactIndex = if (targetFactIndex == 0) facts.size - 1 else targetFactIndex - 1
                    } else if (dragAmount < -50) {
                        targetFactIndex = (targetFactIndex + 1) % facts.size
                    }
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEEF2FF))
                    .graphicsLayer {
                        scaleX = 0.9f + (animatedProgress * 0.1f)
                        scaleY = 0.9f + (animatedProgress * 0.1f)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "💡", fontSize = 20.sp)
            }
            
            Spacer(modifier = Modifier.width(14.dp))
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer {
                        alpha = animatedProgress
                    }
            ) {
                Text(
                    text = "Did you know?",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6366F1)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = facts[currentFactIndex],
                    fontSize = 13.sp,
                    color = Color(0xFF475569),
                    lineHeight = 18.sp
                )
            }
            
            // Dot indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                facts.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == currentFactIndex) Color(0xFF6366F1)
                                else Color(0xFFE2E8F0)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun PopularLanguagesSection() {
    val staticLanguages = listOf(
        LanguageData("JavaScript", 21.0f, "#F7DF1E", "JS"),
        LanguageData("Java", 18.0f, "#B07219", "Jv"),
        LanguageData("Python", 15.5f, "#3776AB", "Py"),
        LanguageData("TypeScript", 15.0f, "#3178C6", "TS"),
        LanguageData("C++", 12.5f, "#F34B7D", "C+"),
        LanguageData("Go", 7.0f, "#00ADD8", "Go"),
        LanguageData("Kotlin", 5.0f, "#F18E33", "Kt"),
        LanguageData("Swift", 3.5f, "#FA7343", "Sw"),
        LanguageData("Rust", 2.5f, "#CE422B", "Rs")
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Popular Languages",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Text(
                    text = "🔥",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Animated Bar Chart
            AnimatedBarChart(
                languages = staticLanguages.take(6),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Language Legend with Icons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                staticLanguages.chunked(3).forEach { rowLanguages ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowLanguages.forEach { lang ->
                            LanguageLegendItem(
                                language = lang,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Fill remaining space if row is incomplete
                        repeat(3 - rowLanguages.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageLegendItem(
    language: LanguageData,
    modifier: Modifier = Modifier
) {
    val langColor = android.graphics.Color.parseColor(language.color).let { Color(it) }
    
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(langColor.copy(alpha = 0.1f))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Language Icon Badge
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(langColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = language.icon,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (language.name == "JavaScript") Color.Black else Color.White
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = language.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF374151),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${language.percentage}%",
                fontSize = 10.sp,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}

@Composable
fun AnimatedBarChart(
    languages: List<LanguageData>,
    modifier: Modifier = Modifier
) {
    val maxPercentage = languages.maxOfOrNull { it.percentage } ?: 100f
    val scope = rememberCoroutineScope()
    
    // Staggered animation for each bar
    val animatedValues = remember {
        languages.map { Animatable(0f) }
    }
    
    LaunchedEffect(Unit) {
        languages.forEachIndexed { index, lang ->
            scope.launch {
                delay(index * 80L)
                animatedValues[index].animateTo(
                    targetValue = lang.percentage / maxPercentage,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
        }
    }
    
    Canvas(modifier = modifier) {
        val barWidth = size.width / (languages.size * 2)
        val spacing = barWidth
        val maxBarHeight = size.height * 0.85f
        
        languages.forEachIndexed { index, lang ->
            val color = android.graphics.Color.parseColor(lang.color).let { Color(it) }
            val barHeight = maxBarHeight * animatedValues[index].value
            val x = spacing / 2 + index * (barWidth + spacing)
            val y = size.height - barHeight
            
            // Draw bar with rounded top
            drawRoundRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
            )
            
            // Draw language label at bottom
            drawContext.canvas.nativeCanvas.apply {
                val textPaint = android.graphics.Paint().apply {
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 24f
                    setColor(android.graphics.Color.parseColor("#6B7280"))
                    isAntiAlias = true
                }
                drawText(
                    lang.icon,
                    x + barWidth / 2,
                    size.height + 4f,
                    textPaint
                )
            }
        }
    }
}

data class LanguageData(
    val name: String,
    val percentage: Float,
    val color: String,
    val icon: String = ""
)

@Composable
fun TrendingReposSection(repos: List<GitHubRepo>, navController: NavHostController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Trending Repos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "🚀", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            itemsIndexed(repos.take(6)) { index, repo ->
                TrendingRepoCard(repo = repo, navController = navController, index = index)
            }
        }
    }
}

@Composable
fun TrendingRepoCard(repo: GitHubRepo, navController: NavHostController, index: Int = 0) {
    // Staggered entrance animation
    var visible by remember { mutableStateOf(false) }
    val animatedAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(400, delayMillis = index * 50),
        label = "repo_alpha"
    )
    val animatedOffset by animateFloatAsState(
        targetValue = if (visible) 0f else 30f,
        animationSpec = tween(400, delayMillis = index * 50, easing = FastOutSlowInEasing),
        label = "repo_offset"
    )
    
    LaunchedEffect(Unit) { visible = true }
    
    Card(
        modifier = Modifier
            .width(260.dp)
            .graphicsLayer {
                alpha = animatedAlpha
                translationX = animatedOffset
            }
            .clickable {
                navController.navigate(Routes.repoDetail(repo.owner.login, repo.name))
            },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Owner and Repo Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(repo.owner.avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Owner Avatar",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3F4F6)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = repo.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF111827),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = repo.owner.login,
                        fontSize = 11.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Description
            Text(
                text = repo.description ?: "No description available",
                fontSize = 12.sp,
                color = Color(0xFF6B7280),
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Language with icon
                if (repo.language != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(getLanguageColor(repo.language).copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(getLanguageColor(repo.language))
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = repo.language,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF374151)
                        )
                    }
                }

                // Stars
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Stars",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = formatNumber(repo.stargazersCount ?: 0),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151)
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Top Developers",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "⭐", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            users.take(3).forEachIndexed { index, user ->
                TrendingUserCard(user = user, navController = navController, index = index)
            }
        }
    }
}

@Composable
fun TrendingUserCard(user: GitHubUser, navController: NavHostController, index: Int = 0) {
    // Staggered entrance animation
    var visible by remember { mutableStateOf(false) }
    val animatedAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(400, delayMillis = index * 80),
        label = "user_alpha"
    )
    val animatedOffset by animateFloatAsState(
        targetValue = if (visible) 0f else 20f,
        animationSpec = tween(400, delayMillis = index * 80, easing = FastOutSlowInEasing),
        label = "user_offset"
    )
    
    LaunchedEffect(Unit) { visible = true }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = animatedAlpha
                translationY = animatedOffset
            }
            .clickable {
                navController.navigate(Routes.userDetail(user.login))
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.avatar_url)
                    .crossfade(true)
                    .build(),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3F4F6)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name ?: user.login,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "@${user.login}",
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF)
                )
            }

            // Followers badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF3F4F6))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFF6B7280),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatNumber(user.followers ?: 0),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF374151)
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Explore by Language",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "🔍", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Language chips with icons - 3x3 grid
        val languages = listOf(
            Triple("Kotlin", Color(0xFFA97BFF), "Kt"),
            Triple("JavaScript", Color(0xFFF1E05A), "JS"),
            Triple("Python", Color(0xFF3572A5), "Py"),
            Triple("TypeScript", Color(0xFF2B7489), "TS"),
            Triple("Java", Color(0xFFB07219), "Jv"),
            Triple("Go", Color(0xFF00ADD8), "Go"),
            Triple("Rust", Color(0xFFDEA584), "Rs"),
            Triple("Swift", Color(0xFFFFAC45), "Sw"),
            Triple("C++", Color(0xFFF34B7D), "C+")
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            languages.chunked(3).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { (lang, color, icon) ->
                        LanguageChip(
                            language = lang,
                            color = color,
                            icon = icon,
                            navController = navController,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageChip(
    language: String,
    color: Color,
    icon: String,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val isLightColor = language == "JavaScript"
    
    Card(
        modifier = modifier.clickable {
            // Navigate to search with language filter
        },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Language icon badge
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLightColor) Color.Black else Color.White
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = language,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF374151),
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