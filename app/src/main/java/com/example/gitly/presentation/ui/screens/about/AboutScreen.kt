package com.example.gitly.presentation.ui.screens.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gitly.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavHostController) {
    val context = LocalContext.current
    val primaryColor = Color(0xFF5B5FC7)
    val backgroundColor = Color(0xFFF5F7FA)
    val cardShape = RoundedCornerShape(16.dp)
    val dividerColor = Color(0xFFE5E7EB)
    
    Scaffold(
        containerColor = backgroundColor
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Icon and Name
            item {
                SectionCard(shape = cardShape) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(primaryColor, Color(0xFF8B5CF6))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.github_icon),
                                contentDescription = "App Icon",
                                modifier = Modifier.size(56.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "Gitly",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Version 1.0.0",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Your GitHub Explorer",
                            fontSize = 14.sp,
                            color = Color(0xFF4B5563),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            // Description
            item {
                SectionCard(shape = cardShape) {
                    Text(
                        text = "About Gitly",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF111827)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Gitly is your companion for exploring GitHub. Discover trending repositories, find talented developers, track your favorites, and get AI-powered insights in one focused place.",
                        fontSize = 14.sp,
                        color = Color(0xFF4B5563),
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FeatureChip(
                            icon = Icons.Outlined.TrendingUp,
                            text = "Trending",
                            modifier = Modifier.weight(1f)
                        )
                        FeatureChip(
                            icon = Icons.Outlined.Search,
                            text = "Explore",
                            modifier = Modifier.weight(1f)
                        )
                        FeatureChip(
                            icon = Icons.Outlined.AutoAwesome,
                            text = "AI Insights",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            // Developer Info
            item {
                SectionCard(shape = cardShape) {
                    Text(
                        text = "Developer",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF111827)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Made with ❤️ by developers who love GitHub and open source.",
                        fontSize = 14.sp,
                        color = Color(0xFF4B5563),
                        lineHeight = 22.sp
                    )
                }
            }
            
            // Contact & Links
            item {
                SectionCard(shape = cardShape) {
                    Text(
                        text = "Connect With Us",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF111827),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    AboutLinkItem(
                        icon = Icons.Outlined.Code,
                        title = "Source Code",
                        subtitle = "View on GitHub",
                        onClick = { openUrl(context, "https://github.com") }
                    )

                    Divider(color = dividerColor)

                    AboutLinkItem(
                        icon = Icons.Outlined.BugReport,
                        title = "Report Issue",
                        subtitle = "Found a bug? Let us know",
                        onClick = { openUrl(context, "https://github.com/issues") }
                    )

                    Divider(color = dividerColor)

                    AboutLinkItem(
                        icon = Icons.Outlined.Star,
                        title = "Rate on Play Store",
                        subtitle = "Share your feedback",
                        onClick = { rateApp(context) }
                    )

                    Divider(color = dividerColor)

                    AboutLinkItem(
                        icon = Icons.Outlined.Share,
                        title = "Share App",
                        subtitle = "Tell your friends",
                        onClick = { shareApp(context) }
                    )
                }
            }
            
            // Legal
            item {
                SectionCard(shape = cardShape) {
                    Text(
                        text = "Legal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF111827),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    AboutLinkItem(
                        icon = Icons.Outlined.Policy,
                        title = "Privacy Policy",
                        subtitle = "How we handle your data",
                        onClick = { openUrl(context, "https://github.com") }
                    )

                    Divider(color = dividerColor)

                    AboutLinkItem(
                        icon = Icons.Outlined.Description,
                        title = "Terms of Service",
                        subtitle = "App usage terms",
                        onClick = { openUrl(context, "https://github.com") }
                    )

                    Divider(color = dividerColor)

                    AboutLinkItem(
                        icon = Icons.Outlined.Security,
                        title = "Licenses",
                        subtitle = "Open source licenses",
                        onClick = { /* Open licenses screen */ }
                    )
                }
            }
            
            // Footer
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "© 2026 Gitly",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Built for GitHub Enthusiasts",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            content = content
        )
    }
}

@Composable
private fun FeatureChip(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF0F1FF)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF5B5FC7),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF5B5FC7)
            )
        }
    }
}

@Composable
private fun AboutLinkItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F1FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF5B5FC7),
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(14.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F2937)
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

private fun openUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun rateApp(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("market://details?id=${context.packageName}")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to browser
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
        }
        context.startActivity(intent)
    }
}

private fun shareApp(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out Gitly!")
            putExtra(
                Intent.EXTRA_TEXT,
                "Discover GitHub like never before with Gitly! 🚀\n\n" +
                        "Explore trending repositories, find talented developers, and get AI-powered insights.\n\n" +
                        "Download now: https://play.google.com/store/apps/details?id=${context.packageName}"
            )
        }
        context.startActivity(Intent.createChooser(intent, "Share Gitly"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
