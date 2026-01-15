package com.example.gitly.presentation.ui.screens.user_detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gitly.R
import com.example.gitly.data.model.GitHubUser
import com.example.gitly.data.repository.GeminiRepository
import com.example.gitly.presentation.navigation.Routes
import com.example.gitly.presentation.ui.components.AnimatedLoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileDetailScreen(
    navController: NavHostController,
    username: String,
    geminiRepository: GeminiRepository? = null
) {
    val viewModel: UserDetailViewModel = viewModel()
    val userDetailState by viewModel.userDetailState.collectAsState()
    val userReposState by viewModel.userReposState.collectAsState()
    val contributionState by viewModel.contributionState.collectAsState()
    val aiSummaryState by viewModel.aiSummaryState.collectAsState()
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // Set GeminiRepository when available
    LaunchedEffect(geminiRepository) {
        geminiRepository?.let { viewModel.setGeminiRepository(it) }
    }

    // Fetch user details when screen loads
    LaunchedEffect(username) {
        viewModel.getUserDetails(username)
    }

    // Observe the userDetailState
    LaunchedEffect(userDetailState) {
        isLoading = userDetailState == null
    }
    
    // AI Summary Dialog
    if (aiSummaryState.showDialog) {
        AiSummaryDialog(
            state = aiSummaryState,
            onDismiss = { viewModel.dismissAiSummaryDialog() }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    AnimatedLoadingScreen()
                }
                userDetailState == null -> {
                    // Error state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Failed to load user profile",
                                color = Color.Red,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { navController.popBackStack() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF64B5F6)
                                )
                            ) {
                                Text("Go Back")
                            }
                        }
                    }
                }
                else -> {
                    // Success state - show profile
                    val user = userDetailState!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Top section with back button (floating over profile)
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Profile content
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Profile Header
                                ProfileHeader(user)
                                
                                Spacer(modifier = Modifier.height(20.dp))
                                
                                Divider(
                                    color = Color(0xFFE0E0E0),
                                    thickness = 1.dp
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Bio Section
                                BioSection(user)
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Stats Section
                                StatsSection(user)
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Generate AI Summary Button
                                GenerateAISummaryButton(
                                    onClick = { viewModel.generateAiSummary() },
                                    isLoading = aiSummaryState.isLoading
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // View Statistics Button
                                ViewStatisticsButton(
                                    onClick = {
                                        navController.navigate(Routes.userStatistics(user.login))
                                    }
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                
                                // Additional Info
                                AdditionalInfoSection(user)
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Popular Repositories
                                PopularRepositoriesSection(
                                    repos = userReposState.repos,
                                    isLoading = userReposState.isLoading,
                                    error = userReposState.error
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Contribution Graph
                                ContributionSection(contributionState = contributionState)
                            }
                            
                            // Floating Back Button
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier
                                    .padding(12.dp)
                                    .align(Alignment.TopStart)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White,
                                    shadowElevation = 8.dp,
                                    tonalElevation = 2.dp
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.Black,
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(user: GitHubUser) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Picture with clean border
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E7EB))
            )
            
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.avatar_url)
                    .crossfade(true)
                    .placeholder(R.drawable.github_icon)
                    .error(R.drawable.github_icon)
                    .build(),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        
        Spacer(modifier = Modifier.height(14.dp))
        
        Text(
            text = user.name ?: user.login,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Text(
            text = "@${user.login}",
            fontSize = 14.sp,
            color = Color(0xFF6366F1),
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(10.dp))
        
        // Type badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFF3F4F6))
                .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Text(
                text = user.type ?: "User",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
fun BioSection(user: GitHubUser) {
    if (!user.bio.isNullOrEmpty()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = user.bio,
                fontSize = 15.sp,
                color = Color(0xFF24292F),
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun StatsSection(user: GitHubUser) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Followers
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatNumber(user.followers ?: 0),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Followers",
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
            
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(36.dp)
                    .background(Color(0xFFE5E7EB))
            )
            
            // Following
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatNumber(user.following ?: 0),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Following",
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
            
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(36.dp)
                    .background(Color(0xFFE5E7EB))
            )
            
            // Repos
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatNumber(user.public_repos ?: 0),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Repos",
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
        }
    }
}

@Composable
fun GenerateAISummaryButton(
    onClick: () -> Unit,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6366F1),
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF6366F1).copy(alpha = 0.6f),
            disabledContentColor = Color.White.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = "✨", fontSize = 16.sp)
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = if (isLoading) "Generating..." else "Generate AI Insights",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ViewStatisticsButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFF374151)
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.BarChart,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color(0xFF374151)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "View Statistics",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AdditionalInfoSection(user: GitHubUser) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Company
        if (user.company != null) {
            InfoRow(
                icon = Icons.Outlined.Business,
                text = user.company
            )
        }
        
        // Location
        if (user.location != null) {
            InfoRow(
                icon = Icons.Outlined.LocationOn,
                text = user.location
            )
        }
        
        // Email
        if (user.email != null) {
            InfoRow(
                icon = Icons.Outlined.Email,
                text = user.email
            )
        }
        
        // Blog/Website
        if (user.blog != null && user.blog.isNotEmpty()) {
            InfoRow(
                icon = Icons.Outlined.Link,
                text = user.blog,
                isLink = true
            )
        }
        
        // Twitter
        if (user.twitter_username != null) {
            InfoRow(
                icon = Icons.Outlined.AlternateEmail,
                text = "@${user.twitter_username}",
                isLink = true
            )
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    text: String,
    isLink: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFF3F4F6)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF6B7280)
            )
        }
        
        Spacer(modifier = Modifier.width(10.dp))
        
        Text(
            text = text,
            fontSize = 13.sp,
            color = if (isLink) Color(0xFF6366F1) else Color(0xFF374151),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PopularRepositoriesSection(
    repos: List<com.example.gitly.data.model.GitHubRepo>,
    isLoading: Boolean,
    error: String?
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Popular repositories",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            TextButton(onClick = { /* Show all repos */ }) {
                Text(
                    text = "Customize your pins",
                    fontSize = 12.sp,
                    color = Color(0xFF64B5F6)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        when {
            isLoading -> {
                // Loading state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF64B5F6),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            error != null -> {
                // Error state
                Text(
                    text = "Failed to load repositories",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
            repos.isEmpty() -> {
                // No repositories
                Text(
                    text = "No repositories found",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                // Display repositories
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Display first 3 or all repos based on expanded state
                    val displayRepos = if (isExpanded) repos else repos.take(3)
                    
                    displayRepos.forEach { repo ->
                        RepositoryCard(
                            name = repo.name,
                            language = repo.language ?: "Unknown",
                            languageColor = getLanguageColor(repo.language ?: ""),
                            stars = repo.stargazersCount ?: 0,
                            isPublic = !repo.private
                        )
                    }
                    
                    // Show "See more" button if there are more than 3 repos
                    if (repos.size > 3) {
                        TextButton(
                            onClick = { isExpanded = !isExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isExpanded) "Show less" else "See more (${repos.size - 3} more)",
                                color = Color(0xFF64B5F6),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper function to get language color
fun getLanguageColor(language: String): Color {
    return when (language.lowercase()) {
        "kotlin" -> Color(0xFFA97BFF)
        "java" -> Color(0xFFB07219)
        "javascript" -> Color(0xFFF1E05A)
        "typescript" -> Color(0xFF2B7489)
        "python" -> Color(0xFF3572A5)
        "html" -> Color(0xFFE34C26)
        "css" -> Color(0xFF563D7C)
        "c++" -> Color(0xFFF34B7D)
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

@Composable
fun RepositoryCard(
    name: String,
    language: String,
    languageColor: Color,
    stars: Int,
    isPublic: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Folder,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF9CA3AF)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6366F1),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            if (isPublic) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isPublic) "Public" else "Private",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isPublic) Color(0xFF16A34A) else Color(0xFFDC2626)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Language indicator
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(languageColor)
                )
                
                Spacer(modifier = Modifier.width(6.dp))
                
                Text(
                    text = language,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
                
                Spacer(modifier = Modifier.width(14.dp))
                
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Stars",
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFFFBBF24)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = stars.toString(),
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
fun ContributionSection(contributionState: ContributionState) {
    // Contribution Graph with real data and legend
    ContributionGraph(contributionState = contributionState)
}

@Composable
fun ContributionGraph(contributionState: ContributionState) {
    var selectedYear by remember { mutableStateOf(2025) }
    
    when {
        contributionState.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF64B5F6),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        contributionState.contributionCalendar != null -> {
            val calendar = contributionState.contributionCalendar

            Column(modifier = Modifier.fillMaxWidth()) {
                // Header with contribution count and year selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${calendar.totalContributions} contributions in the last year",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    
                    // Year selector
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF64B5F6)
                    ) {
                        Text(
                            text = selectedYear.toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Contribution grid (simplified without month labels)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Contribution squares
                    calendar.weeks.forEach { week ->
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            week.contributionDays.forEach { day ->
                                val color = when (day.level) {
                                    0 -> Color(0xFFEBEDF0)
                                    1 -> Color(0xFF9BE9A8)
                                    2 -> Color(0xFF40C463)
                                    3 -> Color(0xFF30A14E)
                                    else -> Color(0xFF216E39)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(11.dp)
                                        .background(color, RoundedCornerShape(2.dp))
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Less",
                        fontSize = 10.sp,
                        color = Color(0xFF656D76)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        val colors = listOf(
                            Color(0xFFEBEDF0),
                            Color(0xFF9BE9A8),
                            Color(0xFF40C463),
                            Color(0xFF30A14E),
                            Color(0xFF216E39)
                        )
                        colors.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(11.dp)
                                    .background(color, RoundedCornerShape(2.dp))
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "More",
                        fontSize = 10.sp,
                        color = Color(0xFF656D76)
                    )
                }
            }
        }

        else -> {
            // Simplified fallback static graph
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Unable to load contributions",
                        fontSize = 14.sp,
                        color = Color(0xFF656D76)
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF64B5F6)
                    ) {
                        Text(
                            text = "2025",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Static fallback - simplified
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(52) { week ->
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            repeat(7) { day ->
                                val intensity = (0..4).random()
                                val color = when (intensity) {
                                    0 -> Color(0xFFEBEDF0)
                                    1 -> Color(0xFF9BE9A8)
                                    2 -> Color(0xFF40C463)
                                    3 -> Color(0xFF30A14E)
                                    else -> Color(0xFF216E39)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(11.dp)
                                        .background(color, RoundedCornerShape(2.dp))
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Legend for fallback
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Less",
                        fontSize = 10.sp,
                        color = Color(0xFF656D76)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        val colors = listOf(
                            Color(0xFFEBEDF0),
                            Color(0xFF9BE9A8),
                            Color(0xFF40C463),
                            Color(0xFF30A14E),
                            Color(0xFF216E39)
                        )
                        colors.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(11.dp)
                                    .background(color, RoundedCornerShape(2.dp))
                            )
                        }
                    }
                    //..
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "More",
                        fontSize = 10.sp,
                        color = Color(0xFF656D76)
                    )
                    //..
                }
            }
        }
    }
}

@Composable
fun AiSummaryDialog(
    state: AiSummaryState,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF9FAFB))
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFEEF2FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "✨", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "AI Insights",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                            Text(
                                text = "Powered by Gitly AI",
                                fontSize = 11.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3F4F6))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                
                Divider(color = Color(0xFFE5E7EB), thickness = 1.dp)
                
                // Content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    when {
                        state.isLoading -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(40.dp),
                                    color = Color(0xFF6366F1),
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = "Analyzing profile...",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF374151)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Gitly AI is generating insights",
                                    fontSize = 13.sp,
                                    color = Color(0xFF9CA3AF)
                                )
                            }
                        }
                        state.error != null -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFEE2E2)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ErrorOutline,
                                        contentDescription = null,
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Something went wrong",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFEF4444)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = state.error,
                                    fontSize = 13.sp,
                                    color = Color(0xFF9CA3AF),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        state.summary != null -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = state.summary,
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    color = Color(0xFF374151)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
