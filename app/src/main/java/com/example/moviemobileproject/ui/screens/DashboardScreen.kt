package com.example.moviemobileproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
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
import com.example.moviemobileproject.navigation.Screen
import com.example.moviemobileproject.ui.components.BottomNavigationBar
import com.example.moviemobileproject.ui.components.MovieCard
import com.example.moviemobileproject.ui.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    movieViewModel: MovieViewModel = viewModel()
) {
    val popularMovies by movieViewModel.popularMovies.collectAsState()
    val categoryMovies by movieViewModel.categoryMovies.collectAsState()
    val savedMovies by movieViewModel.savedMovies.collectAsState()
    val isLoading by movieViewModel.isLoading.collectAsState()
    val errorMessage by movieViewModel.errorMessage.collectAsState()
    val categories = movieViewModel.getMovieCategories()
    
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All Categories") }
    
    // Display movies based on selected category
    val moviesToShow = if (selectedCategory == "All Categories") popularMovies else categoryMovies
    
    Scaffold(        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Category Dropdown with Hamburger Menu Icon
                        Box {
                            IconButton(
                                onClick = { expanded = true }
                            ) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "Categories menu",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                                    .background(Color(0xFF1A1A2E))
                                    .width(180.dp)
                            ) {
                                DropdownMenuItem(                                    text = {
                                        Text(
                                            "All Categories",
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                    },
                                    onClick = {
                                        selectedCategory = "All Categories"
                                        expanded = false
                                        movieViewModel.loadPopularMovies()
                                    }
                                )
                                
                                categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "${getCategoryIcon(category)} $category",
                                                color = Color.White,
                                                fontSize = 14.sp
                                            )
                                        },
                                        onClick = {
                                            selectedCategory = category
                                            expanded = false
                                            movieViewModel.loadMoviesByCategory(category)
                                        }
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                          Text(
                            "MovieApp",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(Screen.Search.route)
                        }
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
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
                }            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item(span = { GridItemSpan(2) }) {
                        // Movies Section Header
                        Text(
                            text = if (selectedCategory == "All Categories") "Popular Movies" else "${getCategoryIcon(selectedCategory)} $selectedCategory Movies",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    
                    if (moviesToShow.isEmpty() && !isLoading) {
                        item(span = { GridItemSpan(2) }) {
                            // Empty state
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "",
                                        fontSize = 48.sp
                                    )
                                    Text(
                                        text = "No movies found",
                                        fontSize = 18.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = if (selectedCategory == "All Categories") "Try refreshing the app" else "in $selectedCategory category",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    } else {
                        items(moviesToShow) { movie ->
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
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


