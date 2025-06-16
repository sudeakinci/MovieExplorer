package com.example.moviemobileproject.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.moviemobileproject.navigation.Screen

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Dashboard", Icons.Default.Home, Screen.Dashboard.route),
        BottomNavItem("Search", Icons.Default.Search, Screen.Search.route),
        BottomNavItem("Saved", Icons.Default.Favorite, Screen.SavedMovies.route),
        BottomNavItem("Profile", Icons.Default.Person, Screen.Profile.route)
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
      NavigationBar(
        containerColor = Color(0xFF1A1A2E),
        contentColor = Color.White
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { 
                    Icon(
                        item.icon, 
                        contentDescription = item.title,
                        tint = if (currentRoute == item.route) Color.White else Color.White.copy(alpha = 0.6f)
                    ) 
                },
                label = { 
                    Text(
                        item.title,
                        color = if (currentRoute == item.route) Color.White else Color.White.copy(alpha = 0.6f)
                    ) 
                },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                    unselectedTextColor = Color.White.copy(alpha = 0.6f),
                    indicatorColor = Color.White.copy(alpha = 0.1f)
                )
            )
        }
    }
}

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)
