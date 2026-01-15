package com.example.gitly.presentation.ui.screens.repo_details

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalContext
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
import com.example.gitly.data.model.GitHubRepo
import com.example.gitly.data.repository.GeminiRepository
import com.example.gitly.presentation.navigation.Routes
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoDetailsScreen(
    owner: String,
    repo: String,
    navController: NavHostController,
    viewModel: RepoDetailsViewModel = viewModel(),
    geminiRepository: GeminiRepository? = null
) {
    val state by viewModel.state.collectAsState()
    val aiSummaryState by viewModel.aiSummaryState.collectAsState()
    val context = LocalContext.current
    
    // Set GeminiRepository when available
    LaunchedEffect(geminiRepository) {
        geminiRepository?.let { viewModel.setGeminiRepository(it) }
    }

    LaunchedEffect(owner, repo) {
        viewModel.loadRepositoryDetails(owner, repo)
    }
    
    // AI Summary Dialog
    if (aiSummaryState.showDialog) {
        RepoAiSummaryDialog(
            state = aiSummaryState,
            repoName = "$owner/$repo",
            onDismiss = { viewModel.dismissAiSummaryDialog() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = repo,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = owner,
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF374151)
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
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = Color(0xFF374151)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF111827)
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFAFAFA))
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
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading repository...",
                            fontSize = 14.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
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
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = "Error",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = state.error ?: "Unknown error",
                            color = Color(0xFFEF4444),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
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
                        navController = navController,
                        onGenerateAiSummary = { viewModel.generateAiSummary() },
                        isAiLoading = aiSummaryState.isLoading
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
    navController: NavHostController,
    onGenerateAiSummary: () -> Unit = {},
    isAiLoading: Boolean = false
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
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
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF3F4F6)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = repository.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "@${repository.owner.login}",
                                fontSize = 13.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                        // Visibility badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (repository.private) Color(0xFFFEE2E2) else Color(0xFFDCFCE7)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (repository.private) "Private" else "Public",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (repository.private) Color(0xFFDC2626) else Color(0xFF16A34A)
                            )
                        }
                    }

                    // Description
                    if (!repository.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = repository.description,
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280),
                            lineHeight = 20.sp
                        )
                    }

                    // Topics
                    if (!repository.topics.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            repository.topics.take(8).forEach { topic ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFF3F4F6)
                                ) {
                                    Text(
                                        text = topic,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF6366F1),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
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
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // AI Insights Button
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = !isAiLoading) { onGenerateAiSummary() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF6366F1)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isAiLoading) {
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
                            text = if (isAiLoading) "Analyzing..." else "AI Insights",
                            fontSize = 13.sp,
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
                            navController.navigate(Routes.repoStatistics(repository.owner.login, repository.name))
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.BarChart,
                            contentDescription = "Statistics",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Statistics",
                            fontSize = 13.sp,
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
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        icon = Icons.Default.Star,
                        label = "Stars",
                        count = repository.stargazersCount ?: 0,
                        color = Color(0xFFFBBF24)
                    )
                    StatItem(
                        icon = Icons.Outlined.AccountTree,
                        label = "Forks",
                        count = repository.forksCount ?: 0,
                        color = Color(0xFF10B981)
                    )
                    StatItem(
                        icon = Icons.Outlined.Visibility,
                        label = "Watchers",
                        count = repository.watchersCount ?: 0,
                        color = Color(0xFF3B82F6)
                    )
                    StatItem(
                        icon = Icons.Outlined.BugReport,
                        label = "Issues",
                        count = repository.openIssuesCount ?: 0,
                        color = Color(0xFFEF4444)
                    )
                }
            }
        }

        // Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Repository Info",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF111827)
                    )
                    
                    if (repository.language != null) {
                        InfoRow(
                            icon = Icons.Outlined.Code,
                            label = "Language",
                            value = repository.language,
                            color = getLanguageColor(repository.language)
                        )
                    }
                    
                    if (repository.createdAt != null) {
                        InfoRow(
                            icon = Icons.Outlined.CalendarMonth,
                            label = "Created",
                            value = formatDate(repository.createdAt),
                            color = Color(0xFF3B82F6)
                        )
                    }
                    
                    if (repository.updatedAt != null) {
                        InfoRow(
                            icon = Icons.Outlined.Update,
                            label = "Updated",
                            value = formatDate(repository.updatedAt),
                            color = Color(0xFF8B5CF6)
                        )
                    }
                    
                    repository.fork?.let { isFork ->
                        if (isFork) {
                            InfoRow(
                                icon = Icons.Outlined.CallSplit,
                                label = "Type",
                                value = "Forked Repository",
                                color = Color(0xFFF59E0B)
                            )
                        }
                    }
                    
                    repository.archived?.let { isArchived ->
                        if (isArchived) {
                            InfoRow(
                                icon = Icons.Outlined.Archive,
                                label = "Status",
                                value = "Archived",
                                color = Color(0xFF6B7280)
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
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionButton(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.OpenInNew,
                    label = "Open on GitHub",
                    color = Color(0xFF374151),
                    onClick = { onOpenBrowser(repository.htmlUrl) }
                )
                
                if (!repository.homepage.isNullOrBlank()) {
                    ActionButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Outlined.Language,
                        label = "Homepage",
                        color = Color(0xFFF59E0B),
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
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = formatNumber(count),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF9CA3AF)
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
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color(0xFF9CA3AF)
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF374151)
            )
        }
    }
}

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    color: Color = Color(0xFF6366F1),
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
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
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

@Composable
fun RepoAiSummaryDialog(
    state: RepoAiSummaryState,
    repoName: String,
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
                                text = repoName,
                                fontSize = 12.sp,
                                color = Color(0xFF9CA3AF),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
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
                                    text = "Analyzing repository...",
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