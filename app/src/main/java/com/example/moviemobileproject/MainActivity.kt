package com.example.moviemobileproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.moviemobileproject.navigation.MovieNavigation
import com.example.moviemobileproject.navigation.Screen
import com.example.moviemobileproject.ui.theme.MovieMobileProjectTheme
import com.example.moviemobileproject.ui.viewmodel.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize Firebase
        Firebase.firestore
        
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
    val authViewModel: AuthViewModel = hiltViewModel()
    
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    
    val startDestination = if (true) {
        Screen.Dashboard.route
    } else {
        Screen.Login.route
    }
    
    MovieNavigation(
        navController = navController,
        startDestination = startDestination,
        authViewModel = authViewModel
    )
}