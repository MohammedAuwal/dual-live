package com.duallive.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.duallive.app.ui.theme.GlassWhite

@Composable
fun MainBottomBar(
    currentScreen: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF040B25).copy(alpha = 0.9f),
        contentColor = Color.White
    ) {
        NavigationBarItem(
            selected = currentScreen == "home",
            onClick = { onNavigate("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", color = Color.White) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Cyan.copy(alpha = 0.2f))
        )
        NavigationBarItem(
            selected = currentScreen == "league_list",
            onClick = { onNavigate("league_list") },
            icon = { Icon(Icons.Default.List, contentDescription = "Leagues") },
            label = { Text("Leagues", color = Color.White) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Cyan.copy(alpha = 0.2f))
        )
        NavigationBarItem(
            selected = currentScreen == "marketplace",
            onClick = { onNavigate("marketplace") },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Marketplace") },
            label = { Text("Market", color = Color.White) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Cyan.copy(alpha = 0.2f))
        )
        NavigationBarItem(
            selected = currentScreen == "profile",
            onClick = { onNavigate("profile") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile", color = Color.White) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Cyan.copy(alpha = 0.2f))
        )
    }
}
