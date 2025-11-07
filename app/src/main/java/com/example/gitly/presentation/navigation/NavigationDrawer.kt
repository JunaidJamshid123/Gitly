package com.example.gitly.presentation.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gitly.R

@Composable
fun NavigationDrawerContent(
    onItemClick: (DrawerItem) -> Unit,
    onClose: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            DrawerHeader()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Main Menu Items
            DrawerMenuItem(
                icon = Icons.Outlined.Settings,
                title = "Settings",
                onClick = {
                    onItemClick(DrawerItem.Settings)
                    onClose()
                }
            )
            
            DrawerMenuItem(
                icon = Icons.Outlined.Favorite,
                title = "Saved Items",
                onClick = {
                    onItemClick(DrawerItem.SavedItems)
                    onClose()
                }
            )
            
            DrawerMenuItem(
                icon = Icons.Outlined.Star,
                title = "Search History",
                onClick = {
                    onItemClick(DrawerItem.History)
                    onClose()
                }
            )
            
            DrawerMenuItem(
                icon = Icons.Outlined.Star,
                title = "Offline Mode",
                onClick = {
                    onItemClick(DrawerItem.OfflineMode)
                    onClose()
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Additional Items
            DrawerMenuItem(
                icon = Icons.Outlined.Star,
                title = "Rate Us",
                onClick = {
                    onItemClick(DrawerItem.RateUs)
                    onClose()
                }
            )
            
            DrawerMenuItem(
                icon = Icons.Outlined.Share,
                title = "Share App",
                onClick = {
                    onItemClick(DrawerItem.ShareApp)
                    onClose()
                }
            )
            
            DrawerMenuItem(
                icon = Icons.Outlined.Info,
                title = "About",
                onClick = {
                    onItemClick(DrawerItem.About)
                    onClose()
                }
            )
            
            DrawerMenuItem(
                icon = Icons.Outlined.Star,
                title = "Help & Support",
                onClick = {
                    onItemClick(DrawerItem.Help)
                    onClose()
                }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Footer
            DrawerFooter()
        }
    }
}

@Composable
fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // GitHub Icon
            Image(
                painter = painterResource(id = R.drawable.github),
                contentDescription = "GitHub Icon",
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = "Gitly",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Explore GitHub",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF64B5F6),
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = title,
            fontSize = 15.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DrawerFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Version 1.0.0",
            fontSize = 11.sp,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Made with ❤️ for GitHub",
            fontSize = 11.sp,
            color = Color.Gray
        )
    }
}

// Drawer menu items enum
sealed class DrawerItem {
    object Settings : DrawerItem()
    object SavedItems : DrawerItem()
    object History : DrawerItem()
    object OfflineMode : DrawerItem()
    object RateUs : DrawerItem()
    object ShareApp : DrawerItem()
    object About : DrawerItem()
    object Help : DrawerItem()
}
