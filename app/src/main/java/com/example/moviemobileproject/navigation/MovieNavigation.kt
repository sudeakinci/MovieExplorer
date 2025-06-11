package com.example.moviemobileproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.moviemobileproject.ui.screens.CategoryScreen
import com.example.moviemobileproject.ui.screens.DashboardScreen
import com.example.moviemobileproject.ui.screens.LoginScreen
import com.example.moviemobileproject.ui.screens.ProfileScreen
import com.example.moviemobileproject.ui.screens.SavedMoviesScreen
import com.example.moviemobileproject.ui.screens.SearchScreen
import com.example.moviemobileproject.ui.screens.SignupScreen

@Composable
fun MovieNavigation(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        
        composable(Screen.Signup.route) {
            SignupScreen(navController = navController)
        }
        
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
        
        composable(Screen.Search.route) {
            SearchScreen(navController = navController)
        }
        
        composable("category/{categoryName}") { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            CategoryScreen(
                navController = navController,
                categoryName = categoryName
            )
        }
        
        composable(Screen.SavedMovies.route) {
            SavedMoviesScreen(navController = navController)
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}
