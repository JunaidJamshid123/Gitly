package com.example.gitly.presentation.ui.screens.repo_detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gitly.R
import com.example.gitly.data.model.GitHubRepo
import com.example.gitly.presentation.navigation.Routes
import com.example.gitly.presentation.ui.components.AnimatedLoadingScreen
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import java.text.SimpleDateFormat
import java.util.*

@OptIn(FlowPreview::class)
@Composable
fun RepoDetailScreen(navController: NavHostController) {
    var searchText by remember { mutableStateOf("") }
    val viewModel: RepoDetailViewModel = viewModel()
    val searchState by viewModel.searchState.collectAsState()

    // Improved debouncing with proper cancellation
    LaunchedEffect(Unit) {
        snapshotFlow { searchText }
            .debounce(1500) // Increased to 1.5 seconds to reduce API calls
            .collect { query ->
                if (query.isNotEmpty() && query.length >= 3) {
                    viewModel.searchRepositories(query)
                }
            }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp)),
                placeholder = {
                    Text(
                        "Search repositories",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color.Gray
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color(0xFFE0E0E0),
                    unfocusedIndicatorColor = Color(0xFFE0E0E0),
                    cursorColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Loading state
            if (searchState.isLoading) {
                AnimatedLoadingScreen()
            }
            // Error state
            if (searchState.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = searchState.error ?: "Unknown error",
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Search results with pagination
            if (!searchState.isLoading && searchState.error == null && searchState.displayedRepos.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(searchState.displayedRepos) { repo ->
                            GitHubRepoSearchResult(
                                repo = repo,
                                navController = navController,
                                isFavorite = searchState.favoriteRepoIds.contains(repo.id),
                                onToggleFavorite = { viewModel.toggleFavorite(repo) }
                            )
                        }
                    }
                    
                    // Pagination Controls
                    if (searchState.totalPages > 1) {
                        PaginationControls(
                            currentPage = searchState.currentPage,
                            totalPages = searchState.totalPages,
                            onPreviousClick = { viewModel.previousPage() },
                            onNextClick = { viewModel.nextPage() }
                        )
                    }
                }
            }

            // Empty state
            if (!searchState.isLoading && searchState.error == null && searchState.allRepos.isEmpty() && searchText.isNotEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No repositories found",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Initial state
            if (searchText.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.code),
                            contentDescription = "GitHub Icon",
                            modifier = Modifier.size(130.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Search for GitHub repositories",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Type at least 3 characters",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Show message when less than 3 characters
            if (searchText.isNotEmpty() && searchText.length < 3) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Type at least 3 characters to search",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GitHubRepoSearchResult(
    repo: GitHubRepo,
    navController: NavHostController,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    var showShareDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable {
                navController.navigate(
                    Routes.repoDetail(
                        repo.owner.login,
                        repo.name
                    )
                )
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header Row: Owner info + Action Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Left: Profile Picture + Repo info
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top
                ) {
                    // Profile Picture with AsyncImage
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(repo.owner.avatarUrl)
                            .crossfade(true)
                            .placeholder(R.drawable.github_icon)
                            .error(R.drawable.github_icon)
                            .build(),
                        contentDescription = "Owner Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color(0xFFE0E0E0), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.width(10.dp))
                    
                    // Owner name and repo name
                    Column {
                        Text(
                            text = "${repo.owner.login}/${repo.name}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64B5F6),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Spacer(modifier = Modifier.height(3.dp))
                        
                        // Repository Description
                        Text(
                            text = repo.description ?: "No description",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                // Right: Action Icons
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Favorite Icon
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (isFavorite) Color(0xFFFF4081) else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    // Share Icon
                    IconButton(
                        onClick = { showShareDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Tags
            if (!repo.topics.isNullOrEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.Start
                ) {
                    repo.topics.take(4).forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFE3F2FD),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 10.sp,
                                color = Color(0xFF1976D2),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Bottom Info: Language, Stars, Forks, Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Language
                    if (repo.language != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(getLanguageColor(repo.language))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = repo.language,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    
                    // Stars
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "⭐",
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatRepoNumber(repo.stargazersCount ?: 0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Forks
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🍴",
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatRepoNumber(repo.forksCount ?: 0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    }
                }
                
                // View Statistics link
                Text(
                    text = "📊",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .clickable {
                            Toast.makeText(context, "View Statistics: ${repo.fullName}", Toast.LENGTH_SHORT).show()
                            // Navigation would be: navController.navigate("repo_statistics/${repo.owner.login}/${repo.name}")
                        }
                        .padding(4.dp)
                )
            }
        }
    }
    
    // Show share dialog when needed
    if (showShareDialog) {
        ShareDialog(
            onDismiss = { showShareDialog = false },
            url = repo.htmlUrl,
            title = "${repo.owner.login}/${repo.name}"
        )
    }
}



// Helper function to format large numbers
fun formatRepoNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> String.format(Locale.US, "%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format(Locale.US, "%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

// Helper function to get language color
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
        else -> Color(0xFF808080) // Gray for unknown languages
    }
}

@Composable
fun ShareDialog(
    onDismiss: () -> Unit,
    url: String,
    title: String
) {
    val context = LocalContext.current
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Share",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Copy Link Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("GitHub URL", url)
                            clipboard.setPrimaryClip(clip)
                            Toast
                                .makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT)
                                .show()
                            onDismiss()
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Copy Link",
                        tint = Color(0xFF64B5F6),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Copy Link",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Text(
                            text = url,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                Divider(color = Color(0xFFE0E0E0))
                
                // Share via... Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "$title\n$url")
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share via")
                            context.startActivity(shareIntent)
                            onDismiss()
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color(0xFF64B5F6),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Share via...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Text(
                            text = "Share to other apps",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Cancel Button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Cancel",
                        color = Color(0xFF64B5F6),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous Button
            IconButton(
                onClick = onPreviousClick,
                enabled = currentPage > 1,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous Page",
                    tint = if (currentPage > 1) Color(0xFF64B5F6) else Color(0xFFE0E0E0),
                    modifier = Modifier.size(28.dp)
                )
            }
            
            // Page Indicator
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF5F5F5)
            ) {
                Text(
                    text = "Page $currentPage of $totalPages",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Next Button
            IconButton(
                onClick = onNextClick,
                enabled = currentPage < totalPages,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next Page",
                    tint = if (currentPage < totalPages) Color(0xFF64B5F6) else Color(0xFFE0E0E0),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
