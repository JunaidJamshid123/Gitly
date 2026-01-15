package com.example.gitly.presentation.ui.screens.statistics

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gitly.data.api.RetrofitClient
import com.example.gitly.presentation.viewmodel.RepoStatsState
import com.example.gitly.presentation.viewmodel.StatisticsViewModel

private val IndigoColor = Color(0xFF6366F1)
private val BorderColor = Color(0xFFE5E7EB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoStatisticsScreen(navController: NavHostController, owner: String, repoName: String) {
    val viewModel = remember { StatisticsViewModel(RetrofitClient.apiService) }
    val statsState by viewModel.repoStatsState.collectAsState()

    LaunchedEffect(owner, repoName) {
        viewModel.fetchRepositoryStatistics(owner, repoName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "📊 Repository Stats", 
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
            is RepoStatsState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = IndigoColor)
                }
            }
            is RepoStatsState.Success -> {
                RepoStatisticsContent(
                    statistics = state.statistics,
                    modifier = Modifier.padding(padding)
                )
            }
            is RepoStatsState.Error -> {
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
fun RepoStatisticsContent(
    statistics: com.example.gitly.data.model.RepositoryStatistics,
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
        // Repository Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(IndigoColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Folder,
                            contentDescription = null,
                            tint = IndigoColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = statistics.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        Text(
                            text = statistics.fullName,
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                
                if (statistics.description != null) {
                    Text(
                        text = statistics.description,
                        fontSize = 14.sp,
                        color = Color(0xFF4B5563),
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                
                if (statistics.language != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(getLanguageColor(statistics.language).copy(alpha = 0.1f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(getLanguageColor(statistics.language))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = statistics.language,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = getLanguageColor(statistics.language)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Main Statistics Section Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Text(text = "📈", fontSize = 18.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Key Metrics",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RepoStatCard(
                title = "Stars",
                value = formatNumber(statistics.stars),
                icon = Icons.Outlined.StarOutline,
                iconColor = Color(0xFFF59E0B),
                animatedProgress = animatedProgress,
                modifier = Modifier.weight(1f)
            )
            RepoStatCard(
                title = "Forks",
                value = formatNumber(statistics.forks),
                icon = Icons.Outlined.AccountTree,
                iconColor = Color(0xFF10B981),
                animatedProgress = animatedProgress,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RepoStatCard(
                title = "Watchers",
                value = formatNumber(statistics.watchers),
                icon = Icons.Outlined.Visibility,
                iconColor = Color(0xFF8B5CF6),
                animatedProgress = animatedProgress,
                modifier = Modifier.weight(1f)
            )
            RepoStatCard(
                title = "Open Issues",
                value = statistics.openIssues.toString(),
                icon = Icons.Outlined.BugReport,
                iconColor = Color(0xFFEF4444),
                animatedProgress = animatedProgress,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Repository Information Section
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Text(text = "📋", fontSize = 18.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Repository Details",
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
            Column(modifier = Modifier.padding(20.dp)) {
                InfoRowWithIcon(
                    icon = Icons.Outlined.Storage,
                    iconColor = Color(0xFF6B7280),
                    label = "Size",
                    value = "${statistics.size} KB"
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = BorderColor
                )
                InfoRowWithIcon(
                    icon = Icons.Outlined.CalendarMonth,
                    iconColor = Color(0xFF10B981),
                    label = "Created",
                    value = statistics.createdAt
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = BorderColor
                )
                InfoRowWithIcon(
                    icon = Icons.Outlined.Update,
                    iconColor = Color(0xFF6366F1),
                    label = "Last Updated",
                    value = statistics.updatedAt
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Activity Summary Section
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Text(text = "⚡", fontSize = 18.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Activity Analysis",
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
            Column(modifier = Modifier.padding(20.dp)) {
                AnimatedActivityStatRow(
                    label = "Community Engagement",
                    value = "${statistics.stars + statistics.forks} interactions",
                    percentage = 100,
                    color = IndigoColor,
                    animatedProgress = animatedProgress
                )
                Spacer(modifier = Modifier.height(20.dp))
                AnimatedActivityStatRow(
                    label = "Stars to Forks Ratio",
                    value = if (statistics.forks > 0) 
                        "${String.format("%.1f", statistics.stars.toFloat() / statistics.forks)}:1" 
                    else "N/A",
                    percentage = if (statistics.forks > 0) 
                        minOf(100, (statistics.stars * 100) / (statistics.stars + statistics.forks)) 
                    else 0,
                    color = Color(0xFF10B981),
                    animatedProgress = animatedProgress
                )
                Spacer(modifier = Modifier.height(20.dp))
                AnimatedActivityStatRow(
                    label = "Issue Health",
                    value = "${statistics.openIssues} open issues",
                    percentage = if (statistics.openIssues < 50) 80 else 40,
                    color = if (statistics.openIssues < 50) Color(0xFF10B981) else Color(0xFFF59E0B),
                    animatedProgress = animatedProgress
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun RepoStatCard(
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
fun InfoRowWithIcon(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color(0xFF1F2937),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun AnimatedActivityStatRow(
    label: String,
    value: String,
    percentage: Int,
    color: Color,
    animatedProgress: Float
) {
    val animatedPercentage by animateFloatAsState(
        targetValue = (percentage / 100f) * animatedProgress,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 100f),
        label = "progress_animation"
    )
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color(0xFF1F2937),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 13.sp,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
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
                    .fillMaxWidth(animatedPercentage)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}
