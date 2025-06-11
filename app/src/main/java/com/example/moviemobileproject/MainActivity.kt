package com.example.moviemobileproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.moviemobileproject.data.repository.AuthRepository
import com.example.moviemobileproject.navigation.MovieNavigation
import com.example.moviemobileproject.navigation.Screen
import com.example.moviemobileproject.ui.theme.MovieMobileProjectTheme
import com.example.moviemobileproject.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieMobileProjectTheme {
                MovieApp()
            }
        }
    }
}

@Composable
fun MovieApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    
    val startDestination = if (/*isAuthenticated?*/ true) {
        Screen.Dashboard.route
    } else {
        Screen.Login.route
    }
    
    MovieNavigation(
        navController = navController,
        startDestination = startDestination
    )
}