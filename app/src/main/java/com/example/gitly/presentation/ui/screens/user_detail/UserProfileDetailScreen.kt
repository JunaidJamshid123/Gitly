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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gitly.R
import com.example.gitly.data.model.GitHubUser
import com.example.gitly.presentation.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileDetailScreen(
    navController: NavHostController,
    username: String
) {
    val viewModel: UserDetailViewModel = viewModel()
    val userDetailState by viewModel.userDetailState.collectAsState()
    val userReposState by viewModel.userReposState.collectAsState()
    val contributionState by viewModel.contributionState.collectAsState()
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Fetch user details when screen loads
    LaunchedEffect(username) {
        viewModel.getUserDetails(username)
    }

    // Observe the userDetailState
    LaunchedEffect(userDetailState) {
        isLoading = userDetailState == null
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    // Loading state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF64B5F6))
                    }
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
                                GenerateAISummaryButton()
                                
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
            .padding(top = 40.dp), // Extra top padding for floating back button
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Picture with gradient border
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Gradient ring effect
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF64B5F6),
                                Color(0xFF42A5F5),
                                Color(0xFF1E88E5)
                            )
                        )
                    )
            )
            
            // White ring
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
            
            // Profile image
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
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Name
        Text(
            text = user.name ?: user.login,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Username with @ symbol
        Text(
            text = "@${user.login}",
            fontSize = 15.sp,
            color = Color(0xFF64B5F6),
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(10.dp))
        
        // Type badge (User/Organization)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFE3F2FD),
            border = BorderStroke(1.dp, Color(0xFF64B5F6).copy(alpha = 0.3f))
        ) {
            Text(
                text = user.type ?: "User",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1976D2),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF8F9FA),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Followers
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formatNumber(user.followers ?: 0),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Followers",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(Color(0xFFE0E0E0))
            )
            
            // Following
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formatNumber(user.following ?: 0),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Following",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(Color(0xFFE0E0E0))
            )
            
            // Repos
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formatNumber(user.public_repos ?: 0),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Repos",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun GenerateAISummaryButton() {
    Button(
        onClick = { /* Handle AI summary generation */ },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF64B5F6),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "AI",
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "Generate AI Insights Summary",
                fontSize = 15.sp,
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
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(12.dp),
        border = ButtonDefaults.outlinedButtonBorder
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text(
                text = "📊",
                fontSize = 20.sp
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "View Statistics",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AdditionalInfoSection(user: GitHubUser) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Company
        if (user.company != null) {
            InfoRow(
                icon = Icons.Default.Star,
                text = user.company
            )
        }
        
        // Location
        if (user.location != null) {
            InfoRow(
                icon = Icons.Default.LocationOn,
                text = user.location
            )
        }
        
        // Email
        if (user.email != null) {
            InfoRow(
                icon = Icons.Default.Email,
                text = user.email
            )
        }
        
        // Blog/Website
        if (user.blog != null && user.blog.isNotEmpty()) {
            InfoRow(
                icon = Icons.Default.Star,
                text = user.blog,
                isLink = true
            )
        }
        
        // Twitter
        if (user.twitter_username != null) {
            InfoRow(
                icon = Icons.Default.Star,
                text = "twitter.com/${user.twitter_username}",
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
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Color.Gray
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (isLink) Color(0xFF64B5F6) else Color.Black,
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
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF64B5F6),
                    modifier = Modifier.weight(1f)
                )
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF5F5F5)
                ) {
                    Text(
                        text = if (isPublic) "Public" else "Private",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Language indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(languageColor)
                )
                
                Spacer(modifier = Modifier.width(6.dp))
                
                Text(
                    text = language,
                    fontSize = 12.sp,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Stars
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Stars",
                    modifier = Modifier.size(14.dp),
                    tint = Color.Gray
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = stars.toString(),
                    fontSize = 12.sp,
                    color = Color.Black
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
