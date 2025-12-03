package com.example.gitly.presentation.ui.screens.AI_Insights

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gitly.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AI_InsightScreeen(navController: NavHostController) {
    val viewModel: AiChatViewModel = viewModel()
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9FAFB)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Search Box at Top with Animation
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Surface(
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Column {
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            placeholder = {
                                Text(
                                    text = "Ask me anything about GitHub...",
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Normal,
                                    letterSpacing = 0.2.sp
                                )
                            },
                            leadingIcon = {
                                Image(
                                    painter = painterResource(id = R.drawable.starss),
                                    contentDescription = "AI Icon",
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                Row {
                                    if (messageText.isNotEmpty()) {
                                        IconButton(onClick = { messageText = "" }) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Clear",
                                                tint = Color.Gray,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = {
                                            if (messageText.isNotBlank()) {
                                                viewModel.sendMessage(messageText)
                                                messageText = ""
                                            }
                                        },
                                        enabled = messageText.isNotBlank()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Send,
                                            contentDescription = "Send",
                                            tint = if (messageText.isNotBlank()) Color(0xFF6366F1) else Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF9FAFB),
                                unfocusedContainerColor = Color(0xFFF9FAFB),
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFFE5E7EB),
                                cursorColor = Color(0xFF6366F1)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )
                        
                        Divider(color = Color(0xFFE5E7EB), thickness = 1.dp)
                    }
                }
            }
            
            // Suggestion Chips (show only when chat is empty or has just welcome message)
            if (chatState.messages.size <= 1) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    SuggestionChips(
                        onChipClick = { query ->
                            messageText = query
                        }
                    )
                }
            }
            
            // Messages List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                state = listState,
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(chatState.messages) { message ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        ChatMessageBubble(
                            message = message,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    navController: NavHostController
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            // AI Avatar with animation
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF6366F1),
                                Color(0xFF8B5CF6)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.starss),
                    contentDescription = "AI",
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        // Message Content with Shadow
        Card(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .animateContentSize(),
            shape = RoundedCornerShape(
                topStart = if (message.isUser) 20.dp else 4.dp,
                topEnd = if (message.isUser) 4.dp else 20.dp,
                bottomStart = 20.dp,
                bottomEnd = 20.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser) 
                    Color(0xFF6366F1) 
                else 
                    Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (message.isUser) 3.dp else 2.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                if (message.isLoading) {
                    LoadingIndicator()
                } else {
                    // Message text with formatting
                    FormattedMessageText(
                        content = message.content,
                        isUser = message.isUser
                    )
                    
                    // Links if available
                    if (message.links.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        message.links.forEach { link ->
                            LinkChip(
                                link = link,
                                navController = navController
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
        
        if (message.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            // User Avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF64B5F6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun FormattedMessageText(content: String, isUser: Boolean) {
    val textColor = if (isUser) Color.White else Color(0xFF1F2937)
    val lines = content.split("\n")
    
    Column {
        lines.forEach { line ->
            when {
                line.trim().startsWith("**") && line.trim().endsWith("**") -> {
                    // Bold headers
                    Text(
                        text = line.trim().removeSurrounding("**"),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.padding(vertical = 4.dp),
                        letterSpacing = 0.2.sp
                    )
                }
                line.trim().startsWith("###") -> {
                    // Headers
                    Text(
                        text = line.trim().removePrefix("###").trim(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.padding(vertical = 5.dp),
                        letterSpacing = 0.2.sp
                    )
                }
                line.trim().startsWith("•") || line.trim().startsWith("-") -> {
                    // Bullet points
                    Row(modifier = Modifier.padding(start = 8.dp, top = 2.dp, bottom = 2.dp)) {
                        Text("• ", color = textColor, fontSize = 14.sp)
                        Text(
                            text = line.trim().removePrefix("•").removePrefix("-").trim(),
                            fontSize = 13.sp,
                            color = textColor,
                            lineHeight = 18.sp
                        )
                    }
                }
                line.matches(Regex("\\d+\\..*")) -> {
                    // Numbered lists
                    Text(
                        text = line.trim(),
                        fontSize = 13.sp,
                        color = textColor,
                        modifier = Modifier.padding(start = 8.dp),
                        lineHeight = 18.sp
                    )
                }
                line.contains("**") -> {
                    // Inline bold text
                    val parts = line.split("**")
                    val annotatedString = buildAnnotatedString {
                        parts.forEachIndexed { index, part ->
                            if (index % 2 == 0) {
                                append(part)
                            } else {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(part)
                                }
                            }
                        }
                    }
                    Text(
                        text = annotatedString,
                        fontSize = 13.sp,
                        color = textColor,
                        lineHeight = 18.sp
                    )
                }
                line.isBlank() -> {
                    Spacer(modifier = Modifier.height(4.dp))
                }
                else -> {
                    Text(
                        text = line,
                        fontSize = 13.sp,
                        color = textColor,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LinkChip(link: MessageLink, navController: NavHostController) {
    val backgroundColor = when (link.type) {
        LinkType.REPOSITORY -> Color(0xFFDCFCE7)
        LinkType.USER -> Color(0xFFDBEAFE)
        LinkType.EXTERNAL -> Color(0xFFFEF3C7)
    }
    
    val textColor = when (link.type) {
        LinkType.REPOSITORY -> Color(0xFF166534)
        LinkType.USER -> Color(0xFF1E40AF)
        LinkType.EXTERNAL -> Color(0xFF92400E)
    }
    
    val icon = when (link.type) {
        LinkType.REPOSITORY -> Icons.Default.Star
        LinkType.USER -> Icons.Default.Person
        LinkType.EXTERNAL -> Icons.Default.Star
    }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                when (link.type) {
                    LinkType.REPOSITORY -> {
                        navController.navigate("repo_details/${link.url}")
                    }
                    LinkType.USER -> {
                        navController.navigate("user_profile/${link.url}")
                    }
                    LinkType.EXTERNAL -> {
                        // Handle external links
                    }
                }
            },
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = link.text,
                fontSize = 13.sp,
                color = textColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Open",
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun LoadingIndicator() {
    var currentDot by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(400)
            currentDot = (currentDot + 1) % 3
        }
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        repeat(3) { index ->
            val scale by animateFloatAsState(
                targetValue = if (currentDot == index) 1.2f else 0.8f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "dot_scale"
            )
            
            Box(
                modifier = Modifier
                    .size((8 * scale).dp)
                    .clip(CircleShape)
                    .background(
                        if (currentDot == index) 
                            Color(0xFF6366F1) 
                        else 
                            Color(0xFFD1D5DB)
                    )
            )
        }
    }
}

@Composable
fun SuggestionChips(onChipClick: (String) -> Unit) {
    val suggestions = listOf(
        "🔥 Trending repositories",
        "👨‍💻 Top developers",
        "🤖 Popular in AI",
        "⚛️ React projects"
    )
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9FAFB))
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = "💡 Quick suggestions",
            fontSize = 13.sp,
            color = Color(0xFF6B7280),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            suggestions.take(2).forEach { suggestion ->
                SuggestionChip(
                    text = suggestion,
                    onClick = { onChipClick(suggestion.substring(2)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            suggestions.drop(2).forEach { suggestion ->
                SuggestionChip(
                    text = suggestion,
                    onClick = { onChipClick(suggestion.substring(2)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SuggestionChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "chip_scale"
    )
    
    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.5.dp, Color(0xFFE0E7FF))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                color = Color(0xFF1F2937),
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}
