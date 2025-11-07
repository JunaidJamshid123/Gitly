package com.example.gitly.presentation.ui.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gitly.data.api.RetrofitClient
import com.example.gitly.presentation.viewmodel.RepoStatsState
import com.example.gitly.presentation.viewmodel.StatisticsViewModel

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
                title = { Text("Repository Statistics", fontSize = 18.sp, fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
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
                    CircularProgressIndicator(color = Color.Black)
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
                    Text(state.message, color = Color.Red)
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Repository Header Card
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
                Text(
                    text = statistics.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = statistics.fullName,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (statistics.description != null) {
                    Text(
                        text = statistics.description,
                        fontSize = 14.sp,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                if (statistics.language != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(getLanguageColor(statistics.language))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = statistics.language,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Statistics
        Text(
            text = "Overview",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RepoStatCard(
                title = "Stars",
                value = formatNumber(statistics.stars),
                icon = "⭐",
                modifier = Modifier.weight(1f)
            )
            RepoStatCard(
                title = "Forks",
                value = formatNumber(statistics.forks),
                icon = "🔀",
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
                icon = "👁️",
                modifier = Modifier.weight(1f)
            )
            RepoStatCard(
                title = "Open Issues",
                value = statistics.openIssues.toString(),
                icon = "⚠️",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Repository Information
        Text(
            text = "Repository Info",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow(label = "Size", value = "${statistics.size} KB")
                Divider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color(0xFFE5E7EB)
                )
                InfoRow(label = "Created", value = statistics.createdAt)
                Divider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color(0xFFE5E7EB)
                )
                InfoRow(label = "Last Updated", value = statistics.updatedAt)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Activity Summary
        Text(
            text = "Activity Summary",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ActivityStatRow(
                    label = "Community Engagement",
                    value = "${statistics.stars + statistics.forks} interactions",
                    percentage = 100
                )
                Spacer(modifier = Modifier.height(16.dp))
                ActivityStatRow(
                    label = "Stars to Forks Ratio",
                    value = if (statistics.forks > 0) 
                        "${String.format("%.1f", statistics.stars.toFloat() / statistics.forks)}:1" 
                    else "N/A",
                    percentage = if (statistics.forks > 0) 
                        minOf(100, (statistics.stars * 100) / (statistics.stars + statistics.forks)) 
                    else 0
                )
                Spacer(modifier = Modifier.height(16.dp))
                ActivityStatRow(
                    label = "Issue Resolution",
                    value = "${statistics.openIssues} open",
                    percentage = if (statistics.openIssues < 50) 80 else 40
                )
            }
        }
    }
}

@Composable
fun RepoStatCard(title: String, value: String, icon: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@Composable
fun ActivityStatRow(label: String, value: String, percentage: Int) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = when {
                percentage >= 70 -> Color(0xFF10B981)
                percentage >= 40 -> Color(0xFFF59E0B)
                else -> Color(0xFFEF4444)
            },
            trackColor = Color(0xFFE5E7EB)
        )
    }
}
