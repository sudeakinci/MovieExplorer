package com.example.moviemobileproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.moviemobileproject.ui.components.BottomNavigationBar
import com.example.moviemobileproject.ui.components.MovieCard
import com.example.moviemobileproject.ui.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navController: NavController,
    categoryName: String,
    movieViewModel: MovieViewModel = viewModel()
) {
    val categoryMovies by movieViewModel.categoryMovies.collectAsState()
    val isLoading by movieViewModel.isLoading.collectAsState()
    val savedMovies by movieViewModel.savedMovies.collectAsState()
    
    LaunchedEffect(categoryName) {
        movieViewModel.loadMoviesByCategory(categoryName)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = categoryName,
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
                },                colors = TopAppBarDefaults.topAppBarColors(
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
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                
                categoryMovies.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No movies found",
                                fontSize = 18.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "in $categoryName category",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
                
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "$categoryName Movies (${categoryMovies.size})",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        
                        items(
                            items = categoryMovies.chunked(2),
                            key = { it.first().id }
                        ) { moviePair ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {                                moviePair.forEach { movie ->
                                    MovieCard(
                                        movie = movie,
                                        onCardClick = {
                                            navController.navigate("movie_details/${movie.id}")
                                        },
                                        isSaved = savedMovies.any { it.movieId == movie.id },
                                        onSaveClick = {
                                            if (savedMovies.any { it.movieId == movie.id }) {
                                                movieViewModel.removeSavedMovie(movie.id)
                                            } else {
                                                movieViewModel.saveMovie(movie)
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                
                                // Add empty space if odd number of movies
                                if (moviePair.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getCategoryIcon(category: String): String {
    return when (category) {
        "Action" -> ""
        "Drama" -> ""
        "Sci-Fi" -> ""
        "Crime" -> ""
        "Romance" -> ""
        "Comedy" -> ""
        "Horror" -> ""
        "Thriller" -> ""
        else -> ""
    }
}
