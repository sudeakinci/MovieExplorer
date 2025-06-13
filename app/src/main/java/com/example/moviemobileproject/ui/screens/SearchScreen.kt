package com.example.moviemobileproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
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
import com.example.moviemobileproject.ui.components.MovieCard
import com.example.moviemobileproject.ui.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    movieViewModel: MovieViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }
    var selectedYear by remember { mutableStateOf("All") }
    var selectedRating by remember { mutableStateOf("All") }
    
    // Temporary filter states (before applying)
    var tempSelectedCategories by remember { mutableStateOf(setOf<String>()) }
    var tempSelectedYear by remember { mutableStateOf("All") }
    var tempSelectedRating by remember { mutableStateOf("All") }
    
    val searchResults by movieViewModel.searchResults.collectAsState()
    val allMovies by movieViewModel.allMovies.collectAsState()
    val isLoading by movieViewModel.isLoading.collectAsState()
    val savedMovies by movieViewModel.savedMovies.collectAsState()
    
    // Load all movies when component starts
    LaunchedEffect(Unit) {
        movieViewModel.loadAllMovies()
    }
    
    // Determine which movies to filter based on search state
    val moviesToFilter = if (searchQuery.isEmpty()) allMovies else searchResults
    
    // Filter the movies based on selected filters
    val filteredResults = remember(moviesToFilter, selectedCategories, selectedYear, selectedRating) {
        moviesToFilter.filter { movie ->
            val categoryMatch = selectedCategories.isEmpty() || selectedCategories.contains(movie.category)
            val yearMatch = selectedYear == "All" || movie.releaseYear.toString() == selectedYear
            val ratingMatch = when (selectedRating) {
                "All" -> true
                "9+" -> movie.rating >= 9.0
                "8+" -> movie.rating >= 8.0
                "7+" -> movie.rating >= 7.0
                "6+" -> movie.rating >= 6.0
                else -> true
            }
            categoryMatch && yearMatch && ratingMatch
        }
    }
    
    // Check if any filters are applied
    val hasActiveFilters = selectedCategories.isNotEmpty() || selectedYear != "All" || selectedRating != "All"
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { 
                            searchQuery = it
                            if (it.isNotBlank()) {
                                movieViewModel.searchMovies(it)
                            } else {
                                movieViewModel.clearSearchResults()
                            }
                        },
                        placeholder = { 
                            Text(
                                "Search movies...", 
                                color = Color.White.copy(alpha = 0.7f)
                            ) 
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White.copy(alpha = 0.8f)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        searchQuery = ""
                                        movieViewModel.clearSearchResults()
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White.copy(alpha = 0.8f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            cursorColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
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
                },                actions = {
                    IconButton(
                        onClick = { 
                            showFilters = !showFilters
                            if (showFilters) {
                                // Initialize temp states with current values when opening filters
                                tempSelectedCategories = selectedCategories
                                tempSelectedYear = selectedYear
                                tempSelectedRating = selectedRating
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filters",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E)
                )
            )
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
                Column {                    // Filter Section
                    if (showFilters) {
                        FilterSection(
                            selectedCategories = tempSelectedCategories,
                            selectedYear = tempSelectedYear,
                            selectedRating = tempSelectedRating,
                            onCategoriesChange = { tempSelectedCategories = it },
                            onYearChange = { tempSelectedYear = it },
                            onRatingChange = { tempSelectedRating = it },
                            onApplyFilters = {
                                selectedCategories = tempSelectedCategories
                                selectedYear = tempSelectedYear
                                selectedRating = tempSelectedRating
                                showFilters = false
                            },
                            onClearFilters = {
                                tempSelectedCategories = setOf()
                                tempSelectedYear = "All"
                                tempSelectedRating = "All"
                                selectedCategories = setOf()
                                selectedYear = "All"
                                selectedRating = "All"
                            },
                            categories = movieViewModel.getMovieCategories()
                        )
                    }
                      // Content Section
                    if (searchQuery.isEmpty() && !hasActiveFilters) {
                        // Search prompt (no search and no filters)
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🔍",
                                fontSize = 48.sp
                            )
                            Text(
                                text = "Search for movies",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                            Text(
                                text = "Type in the search bar above or use filters",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    } else if (filteredResults.isEmpty()) {
                        // No results
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "😔",
                                fontSize = 48.sp
                            )
                            Text(
                                text = if (searchQuery.isEmpty()) "No movies match filters" else if (searchResults.isEmpty()) "No movies found" else "No movies match filters",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                            Text(
                                text = if (searchQuery.isEmpty()) "Try adjusting your filters" else if (searchResults.isEmpty()) "Try a different search term" else "Try adjusting your filters",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }                    } else {                        // Results with counter
                        Column {
                            // Results counter and active filters
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {                                
                                // Show active filters
                                if (hasActiveFilters) {
                                    val activeFilters = mutableListOf<String>()
                                    if (selectedCategories.isNotEmpty()) {
                                        activeFilters.add("Categories: ${selectedCategories.joinToString(", ")}")
                                    }
                                    if (selectedYear != "All") {
                                        activeFilters.add("Year: $selectedYear")
                                    }
                                    if (selectedRating != "All") {
                                        activeFilters.add("Rating: $selectedRating")
                                    }
                                    
                                    if (activeFilters.isNotEmpty()) {
                                        Text(
                                            text = "Filters: ${activeFilters.joinToString(" • ")}",
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.6f),
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                            
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {                                items(filteredResults) { movie ->
                                    MovieCard(
                                        movie = movie,
                                        onCardClick = {
                                            navController.navigate("movie_details/${movie.id}")
                                        },
                                        onSaveClick = { 
                                            if (savedMovies.any { it.movieId == movie.id }) {
                                                movieViewModel.removeSavedMovie(movie.id)
                                            } else {
                                                movieViewModel.saveMovie(movie)
                                            }
                                        },
                                        isSaved = savedMovies.any { it.movieId == movie.id }
                                    )
                                }
                            }
                        }
                    }}
            }
        }
    }
}

@Composable
fun FilterSection(
    selectedCategories: Set<String>,
    selectedYear: String,
    selectedRating: String,
    onCategoriesChange: (Set<String>) -> Unit,
    onYearChange: (String) -> Unit,
    onRatingChange: (String) -> Unit,
    onApplyFilters: () -> Unit,
    onClearFilters: () -> Unit,
    categories: List<String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔧 Filters",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                // Clear filters button
                if (selectedCategories.isNotEmpty() || selectedYear != "All" || selectedRating != "All") {
                    TextButton(
                        onClick = onClearFilters,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White.copy(alpha = 0.8f)
                        )
                    ) {
                        Text(
                            text = "Clear All",
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            // Category Filter
            Text(
                text = "Categories (Multiple Selection)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        label = category,
                        isSelected = selectedCategories.contains(category),
                        onClick = { 
                            val newCategories = if (selectedCategories.contains(category)) {
                                selectedCategories - category
                            } else {
                                selectedCategories + category
                            }
                            onCategoriesChange(newCategories)
                        }
                    )
                }
            }
            
            // Year Filter
            Text(
                text = "Year",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            val years = listOf("All", "2024", "2023", "2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(years) { year ->
                    FilterChip(
                        label = year,
                        isSelected = selectedYear == year,
                        onClick = { onYearChange(year) }
                    )
                }
            }
            
            // Rating Filter
            Text(
                text = "Minimum Rating",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            val ratings = listOf("All", "9+", "8+", "7+", "6+")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(ratings) { rating ->
                    FilterChip(
                        label = rating,
                        isSelected = selectedRating == rating,
                        onClick = { onRatingChange(rating) }
                    )
                }
            }
            
            // Apply Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {                OutlinedButton(
                    onClick = onClearFilters,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear")
                }
                
                Button(
                    onClick = onApplyFilters,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply Filters")
                }
            }
        }
    }
}

@Composable
fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    androidx.compose.material3.FilterChip(
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontSize = 12.sp,
                color = if (isSelected) Color.Black else Color.White
            )
        },
        selected = isSelected,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color.White,
            selectedLabelColor = Color.Black,
            containerColor = Color.Transparent,
            labelColor = Color.White
        )
    )
}
