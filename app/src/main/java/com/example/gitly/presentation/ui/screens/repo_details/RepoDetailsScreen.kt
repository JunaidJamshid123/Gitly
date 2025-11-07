package com.example.gitly.presentation.ui.screens.repo_details

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gitly.data.model.GitHubRepo
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoDetailsScreen(
    owner: String,
    repo: String,
    navController: NavHostController,
    viewModel: RepoDetailsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(owner, repo) {
        viewModel.loadRepositoryDetails(owner, repo)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "$owner/$repo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            state.repository?.let { repository ->
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "${repository.owner.login}/${repository.name}")
                                    putExtra(Intent.EXTRA_TEXT, 
                                        "Check out ${repository.name} by ${repository.owner.login} on GitHub!\n\n${repository.htmlUrl}")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Repository"))
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF64B5F6)
                    )
                }
                state.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Error",
                            tint = Color.Red,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = state.error ?: "Unknown error",
                            color = Color.Red,
                            fontSize = 16.sp
                        )
                    }
                }
                state.repository != null -> {
                    RepoDetailsContent(
                        repository = state.repository!!,
                        onOpenBrowser = { url ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        },
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun RepoDetailsContent(
    repository: GitHubRepo,
    onOpenBrowser: (String) -> Unit,
    navController: NavHostController
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Owner Info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(repository.owner.avatarUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Owner Avatar",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = repository.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = repository.owner.login,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    // Description
                    if (!repository.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = repository.description,
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            lineHeight = 20.sp
                        )
                    }

                    // Topics
                    if (!repository.topics.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repository.topics.take(10).forEach { topic ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFFE3F2FD)
                                ) {
                                    Text(
                                        text = topic,
                                        fontSize = 12.sp,
                                        color = Color(0xFF1976D2),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Actions: AI Insights & Statistics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // AI Insights Button
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            // Navigate to AI Insights
                            // You can implement this based on your AI screen
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF64B5F6)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "AI Insights",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Insights",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                // Statistics Button
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navController.navigate("repo_statistics/${repository.owner.login}/${repository.name}")
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF66BB6A)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Statistics",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Statistics",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Stats Card
        item {
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
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        icon = Icons.Default.Star,
                        label = "Stars",
                        count = repository.stargazersCount ?: 0,
                        color = Color(0xFFFFA726)
                    )
                    StatItem(
                        icon = Icons.Default.Star,
                        label = "Forks",
                        count = repository.forksCount ?: 0,
                        color = Color(0xFF66BB6A)
                    )
                    StatItem(
                        icon = Icons.Default.Star,
                        label = "Watchers",
                        count = repository.watchersCount ?: 0,
                        color = Color(0xFF42A5F5)
                    )
                    StatItem(
                        icon = Icons.Default.Star,
                        label = "Issues",
                        count = repository.openIssuesCount ?: 0,
                        color = Color(0xFFEF5350)
                    )
                }
            }
        }

        // Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    
                    if (repository.language != null) {
                        InfoRow(
                            icon = Icons.Default.Star,
                            label = "Language",
                            value = repository.language,
                            color = getLanguageColor(repository.language)
                        )
                    }
                    
                    if (repository.visibility != null) {
                        InfoRow(
                            icon = if (repository.private) Icons.Default.Lock else Icons.Default.Star,
                            label = "Visibility",
                            value = repository.visibility.capitalize(Locale.ROOT),
                            color = if (repository.private) Color(0xFFEF5350) else Color(0xFF66BB6A)
                        )
                    }
                    
                    if (repository.createdAt != null) {
                        InfoRow(
                            icon = Icons.Default.Star,
                            label = "Created",
                            value = formatDate(repository.createdAt),
                            color = Color(0xFF42A5F5)
                        )
                    }
                    
                    if (repository.updatedAt != null) {
                        InfoRow(
                            icon = Icons.Default.Star,
                            label = "Updated",
                            value = formatDate(repository.updatedAt),
                            color = Color(0xFF9C27B0)
                        )
                    }
                    
                    repository.fork?.let { isFork ->
                        if (isFork) {
                            InfoRow(
                                icon = Icons.Default.Star,
                                label = "Type",
                                value = "Forked Repository",
                                color = Color(0xFFFF9800)
                            )
                        }
                    }
                    
                    repository.archived?.let { isArchived ->
                        if (isArchived) {
                            InfoRow(
                                icon = Icons.Default.Star,
                                label = "Status",
                                value = "Archived",
                                color = Color(0xFF757575)
                            )
                        }
                    }
                }
            }
        }

        // Links Card
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Star,
                    label = "GitHub",
                    color = Color(0xFF9C27B0),
                    onClick = { onOpenBrowser(repository.htmlUrl) }
                )
                
                if (!repository.homepage.isNullOrBlank()) {
                    ActionButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Home,
                        label = "Homepage",
                        color = Color(0xFFFF9800),
                        onClick = { onOpenBrowser(repository.homepage) }
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    label: String,
    count: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = formatNumber(count),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    color: Color = Color(0xFF64B5F6),
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = color
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

// Helper function for FlowRow
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val sequences = mutableListOf<List<Placeable>>()
        val currentSequence = mutableListOf<Placeable>()
        var currentWidth = 0
        var currentHeight = 0
        var maxHeight = 0

        measurables.forEach { measurable ->
            val placeable = measurable.measure(constraints)
            
            if (currentWidth + placeable.width > constraints.maxWidth && currentSequence.isNotEmpty()) {
                sequences.add(currentSequence.toList())
                currentSequence.clear()
                maxHeight += currentHeight + 8.dp.roundToPx()
                currentWidth = 0
                currentHeight = 0
            }
            
            currentSequence.add(placeable)
            currentWidth += placeable.width + 8.dp.roundToPx()
            currentHeight = maxOf(currentHeight, placeable.height)
        }
        
        if (currentSequence.isNotEmpty()) {
            sequences.add(currentSequence)
            maxHeight += currentHeight
        }

        layout(constraints.maxWidth, maxHeight) {
            var yPosition = 0
            sequences.forEach { sequence ->
                var xPosition = 0
                var lineHeight = 0
                sequence.forEach { placeable ->
                    placeable.placeRelative(x = xPosition, y = yPosition)
                    xPosition += placeable.width + 8.dp.roundToPx()
                    lineHeight = maxOf(lineHeight, placeable.height)
                }
                yPosition += lineHeight + 8.dp.roundToPx()
            }
        }
    }
}

fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dateString)
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}

fun getLanguageColor(language: String): Color {
    return when (language.lowercase()) {
        "javascript" -> Color(0xFFF7DF1E)
        "typescript" -> Color(0xFF3178C6)
        "python" -> Color(0xFF3776AB)
        "java" -> Color(0xFFB07219)
        "kotlin" -> Color(0xFF9B7EDE)
        "swift" -> Color(0xFFFA7343)
        "go" -> Color(0xFF00ADD8)
        "rust" -> Color(0xFFCE422B)
        "c++" -> Color(0xEF5FA7)
        "c" -> Color(0xFFA8B9CC)
        "ruby" -> Color(0xFF701516)
        "php" -> Color(0xFF4F5D95)
        else -> Color(0xFF95A5A6)
    }
}

@Composable
private fun Layout(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    measurePolicy: androidx.compose.ui.layout.MeasurePolicy
) {
    androidx.compose.ui.layout.Layout(
        content = content,
        modifier = modifier,
        measurePolicy = measurePolicy
    )
}
