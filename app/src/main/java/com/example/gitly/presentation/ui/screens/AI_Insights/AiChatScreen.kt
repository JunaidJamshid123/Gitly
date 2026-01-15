package com.example.gitly.presentation.ui.screens.AI_Insights

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Modern Color Palette
private val BackgroundColor = Color(0xFFFAFAFC)
private val SurfaceColor = Color.White
private val PrimaryColor = Color(0xFF5B5FC7)
private val PrimaryLightColor = Color(0xFFEEEFF8)
private val AccentColor = Color(0xFF10B981)
private val TextPrimaryColor = Color(0xFF1A1A2E)
private val TextSecondaryColor = Color(0xFF6B7280)
private val TextTertiaryColor = Color(0xFF9CA3AF)
private val BorderColor = Color(0xFFE5E7EB)
private val UserBubbleColor = Color(0xFF5B5FC7)
private val AiBubbleColor = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AI_InsightScreeen(navController: NavHostController) {
    val viewModel: AiChatViewModel = hiltViewModel()
    val chatState by viewModel.chatState.collectAsState()
    
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(chatState.messages.size) {
        if (chatState.messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(chatState.messages.size - 1)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Clean Header
            ChatHeader()
            
            // Main Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (chatState.messages.size <= 1) {
                    // Welcome Screen with suggestions
                    WelcomeContent(
                        onSuggestionClick = { query ->
                            viewModel.sendMessage(query)
                        }
                    )
                } else {
                    // Chat Messages
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        state = listState,
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(
                            items = chatState.messages.drop(1), // Skip welcome message
                            key = { it.id }
                        ) { message ->
                            ChatMessageItem(
                                message = message,
                                navController = navController
                            )
                        }
                    }
                }
            }
            
            // Input Area
            ChatInputArea(
                messageText = messageText,
                onMessageChange = { messageText = it },
                onSendClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(messageText)
                        messageText = ""
                    }
                },
                isProcessing = chatState.isProcessing
            )
        }
    }
}

@Composable
private fun ChatHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceColor,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // AI Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                PrimaryColor,
                                Color(0xFF8B5CF6)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(14.dp))
            
            Column {
                Text(
                    text = "Gitly AI",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimaryColor,
                    letterSpacing = (-0.3).sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(AccentColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Online • GitHub Assistant",
                        fontSize = 13.sp,
                        color = TextSecondaryColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Options button
            IconButton(
                onClick = { /* TODO: Options menu */ },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "Options",
                    tint = TextSecondaryColor
                )
            }
        }
    }
}

@Composable
private fun WelcomeContent(
    onSuggestionClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Animated AI Icon
        val infiniteTransition = rememberInfiniteTransition(label = "welcome_animation")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = EaseInOutQuad),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )
        
        Box(
            modifier = Modifier
                .size(80.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            PrimaryColor.copy(alpha = 0.15f),
                            Color(0xFF8B5CF6).copy(alpha = 0.15f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PrimaryColor, Color(0xFF8B5CF6))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "How can I help you today?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryColor,
            letterSpacing = (-0.5).sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "I can help you discover repositories, find developers,\nand explore the GitHub ecosystem.",
            fontSize = 15.sp,
            color = TextSecondaryColor,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Suggestion Categories
        SuggestionCategories(onSuggestionClick = onSuggestionClick)
    }
}

@Composable
private fun SuggestionCategories(
    onSuggestionClick: (String) -> Unit
) {
    val categories = listOf(
        SuggestionCategory(
            icon = Icons.Outlined.TrendingUp,
            title = "Trending",
            suggestions = listOf(
                "Show me trending repositories this week",
                "What's popular in open source right now?"
            ),
            color = Color(0xFF10B981)
        ),
        SuggestionCategory(
            icon = Icons.Outlined.Code,
            title = "Discover",
            suggestions = listOf(
                "Best Kotlin Android libraries",
                "Top machine learning projects"
            ),
            color = Color(0xFF3B82F6)
        ),
        SuggestionCategory(
            icon = Icons.Outlined.Person,
            title = "Developers",
            suggestions = listOf(
                "Most followed developers",
                "Find React Native experts"
            ),
            color = Color(0xFFF59E0B)
        ),
        SuggestionCategory(
            icon = Icons.Outlined.Lightbulb,
            title = "Ideas",
            suggestions = listOf(
                "Project ideas for beginners",
                "Interesting APIs to explore"
            ),
            color = Color(0xFFEC4899)
        )
    )
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        categories.chunked(2).forEach { rowCategories ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowCategories.forEach { category ->
                    SuggestionCard(
                        category = category,
                        onClick = { onSuggestionClick(category.suggestions.first()) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

data class SuggestionCategory(
    val icon: ImageVector,
    val title: String,
    val suggestions: List<String>,
    val color: Color
)

@Composable
private fun SuggestionCard(
    category: SuggestionCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_scale"
    )
    
    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = cardScale
                scaleY = cardScale
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        color = SurfaceColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(category.color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    tint = category.color,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = category.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimaryColor
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = category.suggestions.first(),
                fontSize = 13.sp,
                color = TextSecondaryColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}

@Composable
private fun ChatMessageItem(
    message: ChatMessage,
    navController: NavHostController
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            if (!message.isUser) {
                // AI Avatar
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(PrimaryColor, Color(0xFF8B5CF6))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
            
            // Message Bubble
            Surface(
                modifier = Modifier.widthIn(max = 300.dp),
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (message.isUser) 20.dp else 6.dp,
                    bottomEnd = if (message.isUser) 6.dp else 20.dp
                ),
                color = if (message.isUser) UserBubbleColor else AiBubbleColor,
                shadowElevation = if (message.isUser) 0.dp else 2.dp,
                border = if (!message.isUser) androidx.compose.foundation.BorderStroke(1.dp, BorderColor) else null
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    if (message.isLoading) {
                        TypingIndicator()
                    } else {
                        MessageContent(
                            content = message.content,
                            isUser = message.isUser
                        )
                        
                        // Links
                        if (message.links.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            message.links.forEach { link ->
                                LinkButton(link = link, navController = navController)
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }
                }
            }
            
            if (message.isUser) {
                Spacer(modifier = Modifier.width(10.dp))
                // User Avatar
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF64748B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageContent(
    content: String,
    isUser: Boolean
) {
    val textColor = if (isUser) Color.White else TextPrimaryColor
    val lines = content.split("\n")
    
    Column {
        lines.forEach { line ->
            when {
                line.trim().startsWith("###") -> {
                    Text(
                        text = line.trim().removePrefix("###").trim(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
                line.trim().startsWith("**") && line.trim().endsWith("**") -> {
                    Text(
                        text = line.trim().removeSurrounding("**"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                line.trim().startsWith("•") || line.trim().startsWith("-") -> {
                    Row(
                        modifier = Modifier.padding(start = 4.dp, top = 3.dp, bottom = 3.dp)
                    ) {
                        Text(
                            text = "•",
                            color = if (isUser) Color.White.copy(alpha = 0.7f) else PrimaryColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = line.trim().removePrefix("•").removePrefix("-").trim(),
                            fontSize = 14.sp,
                            color = textColor,
                            lineHeight = 20.sp
                        )
                    }
                }
                line.matches(Regex("\\d+\\..*")) -> {
                    Text(
                        text = line.trim(),
                        fontSize = 14.sp,
                        color = textColor,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 2.dp),
                        lineHeight = 20.sp
                    )
                }
                line.contains("**") -> {
                    val parts = line.split("**")
                    val annotatedString = buildAnnotatedString {
                        parts.forEachIndexed { index, part ->
                            if (index % 2 == 0) {
                                append(part)
                            } else {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                    append(part)
                                }
                            }
                        }
                    }
                    Text(
                        text = annotatedString,
                        fontSize = 14.sp,
                        color = textColor,
                        lineHeight = 20.sp
                    )
                }
                line.isBlank() -> {
                    Spacer(modifier = Modifier.height(6.dp))
                }
                else -> {
                    Text(
                        text = line,
                        fontSize = 14.sp,
                        color = textColor,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun LinkButton(
    link: MessageLink,
    navController: NavHostController
) {
    val (backgroundColor, iconColor, icon) = when (link.type) {
        LinkType.REPOSITORY -> Triple(
            Color(0xFFECFDF5),
            Color(0xFF059669),
            Icons.Outlined.FolderOpen
        )
        LinkType.USER -> Triple(
            Color(0xFFEFF6FF),
            Color(0xFF2563EB),
            Icons.Outlined.Person
        )
        LinkType.EXTERNAL -> Triple(
            Color(0xFFFFFBEB),
            Color(0xFFD97706),
            Icons.Outlined.OpenInNew
        )
    }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                when (link.type) {
                    LinkType.REPOSITORY -> navController.navigate("repo_details/${link.url}")
                    LinkType.USER -> navController.navigate("user/${link.url}")
                    LinkType.EXTERNAL -> { /* Handle external */ }
                }
            },
        shape = RoundedCornerShape(10.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = link.text,
                fontSize = 13.sp,
                color = iconColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = iconColor.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
    ) {
        repeat(3) { index ->
            val delay = index * 150
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = delay),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_alpha_$index"
            )
            
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(PrimaryColor.copy(alpha = alpha))
            )
        }
    }
}

@Composable
private fun ChatInputArea(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isProcessing: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceColor,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Quick Actions
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                val quickActions = listOf(
                    "🔥 Trending" to "Show trending repositories",
                    "⭐ Popular" to "Most starred repos this month",
                    "🆕 New" to "Recently created projects"
                )
                
                items(quickActions) { (label, query) ->
                    QuickActionChip(
                        label = label,
                        onClick = { onMessageChange(query) }
                    )
                }
            }
            
            // Input Field
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp, max = 120.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = BackgroundColor,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (messageText.isNotEmpty()) PrimaryColor.copy(alpha = 0.5f) else BorderColor
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = messageText,
                            onValueChange = onMessageChange,
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(
                                fontSize = 15.sp,
                                color = TextPrimaryColor,
                                lineHeight = 20.sp
                            ),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (messageText.isEmpty()) {
                                        Text(
                                            text = "Ask me anything about GitHub...",
                                            fontSize = 15.sp,
                                            color = TextTertiaryColor
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                        
                        if (messageText.isNotEmpty()) {
                            IconButton(
                                onClick = { onMessageChange("") },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextTertiaryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(10.dp))
                
                // Send Button
                val sendButtonColor by animateColorAsState(
                    targetValue = if (messageText.isNotBlank() && !isProcessing) 
                        PrimaryColor else Color(0xFFE5E7EB),
                    animationSpec = tween(200),
                    label = "send_button_color"
                )
                
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable(
                            enabled = messageText.isNotBlank() && !isProcessing,
                            onClick = onSendClick
                        ),
                    shape = CircleShape,
                    color = sendButtonColor
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(22.dp)
                                    .graphicsLayer { rotationZ = -45f }
                            )
                        }
                    }
                }
            }
            
            // Powered by label
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Powered by Gitly AI",
                fontSize = 11.sp,
                color = TextTertiaryColor,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun QuickActionChip(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = PrimaryLightColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryColor.copy(alpha = 0.2f))
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = PrimaryColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

private val EaseInOutQuad: Easing = Easing { fraction ->
    if (fraction < 0.5f) {
        2f * fraction * fraction
    } else {
        1f - (-2f * fraction + 2f).let { it * it } / 2f
    }
}
