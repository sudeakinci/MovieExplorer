package com.example.moviemobileproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun WatchedMoviesScreen(
    navController: NavController,
    movieViewModel: MovieViewModel = viewModel()
) {
    val watchedMovies by movieViewModel.watchedMovies.collectAsState()
    val isLoading by movieViewModel.isLoading.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Watched Movies",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
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
                if (watchedMovies.isEmpty()) {
                    // Empty state
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "",
                            fontSize = 48.sp
                        )
                        Text(
                            text = "No watched movies yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        Text(
                            text = "Start watching and track your progress!",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(watchedMovies) { watchedMovie ->
                            val movie = Movie(
                                id = watchedMovie.movieId,
                                title = watchedMovie.title,
                                imageUrl = watchedMovie.imageUrl,
                                category = watchedMovie.category,
                                description = watchedMovie.description,
                                rating = watchedMovie.rating,
                                releaseYear = watchedMovie.releaseYear,
                                isPopular = false
                            )
                            MovieCard(
                                movie = movie,
                                onCardClick = {
                                    navController.navigate("movie_details/${watchedMovie.movieId}")
                                },
                                onSaveClick = { 
                                    movieViewModel.removeWatchedMovie(watchedMovie.movieId)
                                },
                                isSaved = false // Watched movies are not shown as saved
                            )
                        }
                    }
                }
            }
        }
    }
}
