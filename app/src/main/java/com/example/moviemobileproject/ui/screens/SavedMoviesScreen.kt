package com.example.moviemobileproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moviemobileproject.data.model.Movie
import com.example.moviemobileproject.ui.components.BottomNavigationBar
import com.example.moviemobileproject.ui.components.MovieCard
import com.example.moviemobileproject.ui.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedMoviesScreen(
    navController: NavController,
    movieViewModel: MovieViewModel = viewModel()
) {
    val savedMovies by movieViewModel.savedMovies.collectAsState()
    val isLoading by movieViewModel.isLoading.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "💖 Saved Movies",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E)
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A1A2E),
                            Color(0xFF16213E),
                            Color(0xFF0F3460)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                if (savedMovies.isEmpty()) {
                    // Empty state
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "💔",
                            fontSize = 48.sp
                        )
                        Text(
                            text = "No saved movies yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        Text(
                            text = "Start exploring and save your favorites!",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(savedMovies) { savedMovie ->
                            val movie = Movie(
                                id = savedMovie.movieId,
                                title = savedMovie.title,
                                imageUrl = savedMovie.imageUrl,
                                category = savedMovie.category,
                                description = savedMovie.description,
                                isPopular = false
                            )
                            MovieCard(
                                movie = movie,
                                onCardClick = {
                                    navController.navigate("movie_details/${savedMovie.movieId}")
                                },
                                onSaveClick = { 
                                    movieViewModel.removeSavedMovie(savedMovie.movieId)
                                },
                                isSaved = true
                            )
                        }
                    }
                }
            }
        }
    }
}
