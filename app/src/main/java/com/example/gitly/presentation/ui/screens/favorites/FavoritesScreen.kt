package com.example.gitly.presentation.ui.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gitly.R
import com.example.gitly.data.local.entity.FavoriteRepo
import com.example.gitly.data.local.entity.FavoriteUserEntity
import java.text.NumberFormat
import java.util.Locale
@Composable
fun FavoritesScreen(navController: NavHostController) {
    val viewModel: FavoritesViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Toggle Tabs
            TabSelector(
                selectedTab = state.selectedTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Content based on selected tab
            when (state.selectedTab) {
                FavoriteTab.REPOS -> {
                    FavoriteReposList(
                        repos = state.favoriteRepos,
                        navController = navController,
                        onRemove = { viewModel.removeFavoriteRepo(it) }
                    )
                }
                FavoriteTab.USERS -> {
                    FavoriteUsersList(
                        users = state.favoriteUsers,
                        navController = navController,
                        onRemove = { viewModel.removeFavoriteUser(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun TabSelector(selectedTab: FavoriteTab, onTabSelected: (FavoriteTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TabButton(
            text = "Repositories",
            isSelected = selectedTab == FavoriteTab.REPOS,
            onClick = { onTabSelected(FavoriteTab.REPOS) },
            modifier = Modifier.weight(1f)
        )
        TabButton(
            text = "Users",
            isSelected = selectedTab == FavoriteTab.USERS,
            onClick = { onTabSelected(FavoriteTab.USERS) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Color.White else Color.Transparent
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color(0xFF64B5F6) else Color.Gray
            )
        }
    }
}

@Composable
fun FavoriteReposList(
    repos: List<FavoriteRepo>,
    navController: NavHostController,
    onRemove: (Long) -> Unit
) {
    if (repos.isEmpty()) {
        EmptyState(
            icon = Icons.Default.Star,
            message = "No favorite repositories yet",
            description = "Tap the star icon on any repository to save it here"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(repos) { repo ->
                FavoriteRepoCard(
                    repo = repo,
                    navController = navController,
                    onRemove = { onRemove(repo.id) }
                )
            }
        }
    }
}

@Composable
fun FavoriteUsersList(
    users: List<FavoriteUserEntity>,
    navController: NavHostController,
    onRemove: (Int) -> Unit
) {
    if (users.isEmpty()) {
        EmptyState(
            icon = Icons.Default.Person,
            message = "No favorite users yet",
            description = "Tap the star icon on any user to save them here"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(users) { user ->
                FavoriteUserCard(
                    user = user,
                    navController = navController,
                    onRemove = { onRemove(user.id) }
                )
            }
        }
    }
}

@Composable
fun FavoriteRepoCard(
    repo: FavoriteRepo,
    navController: NavHostController,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("repo_details/${repo.ownerLogin}/${repo.name}")
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(repo.ownerAvatarUrl)
                            .crossfade(true)
                            .placeholder(R.drawable.github_icon)
                            .error(R.drawable.github_icon)
                            .build(),
                        contentDescription = "Owner Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.width(10.dp))
                    
                    Column {
                        Text(
                            text = repo.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = repo.ownerLogin,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Remove from favorites",
                        tint = Color(0xFFFF4081),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // Description
            if (!repo.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = repo.description,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (repo.language != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(getLanguageColor(repo.language))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = repo.language,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    
                    Text(
                        text = "⭐ ${formatNumber(repo.stargazersCount)}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    
                    Text(
                        text = "🔀 ${formatNumber(repo.forksCount)}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteUserCard(
    user: FavoriteUserEntity,
    navController: NavHostController,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("user/${user.login}")
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user.avatarUrl)
                            .crossfade(true)
                            .placeholder(R.drawable.github_icon)
                            .error(R.drawable.github_icon)
                            .build(),
                        contentDescription = "User Avatar",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color(0xFFE0E0E0), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.width(10.dp))
                    
                    Column {
                        Text(
                            text = user.name ?: user.login,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Spacer(modifier = Modifier.height(3.dp))
                        
                        Text(
                            text = "@${user.login}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF64B5F6),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Remove from favorites",
                        tint = Color(0xFFFF4081),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            if (!user.bio.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = user.bio,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Followers",
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${formatNumber(user.followers)} followers",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                
                Text(
                    text = "📦 ${user.publicRepos} repos",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    message: String,
    description: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color(0xFFE0E0E0)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

// Helper functions
fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> String.format(Locale.US, "%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format(Locale.US, "%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

fun getLanguageColor(language: String): Color {
    return when (language.lowercase()) {
        "javascript" -> Color(0xFFF1E05A)
        "java" -> Color(0xFFB07219)
        "python" -> Color(0xFF3572A5)
        "typescript" -> Color(0xFF2B7489)
        "kotlin" -> Color(0xFFA97BFF)
        "swift" -> Color(0xFFFFAC45)
        "go" -> Color(0xFF00ADD8)
        "rust" -> Color(0xFFDEA584)
        "c++" -> Color(0xFFF34B7D)
        "c#" -> Color(0xFF178600)
        "ruby" -> Color(0xFF701516)
        "php" -> Color(0xFF4F5D95)
        "html" -> Color(0xFFE34C26)
        "css" -> Color(0xFF563D7C)
        else -> Color(0xFF808080)
    }
}
