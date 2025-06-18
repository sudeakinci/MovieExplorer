package com.example.moviemobileproject.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import com.example.moviemobileproject.ui.components.BottomNavigationBar
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
    val moviesToFilter = if (searchQuery.isEmpty()) allMovies else searchResults      // Filter the movies based on selected filters
    val filteredResults = remember(moviesToFilter, selectedCategories, selectedYear, selectedRating) {
        val filtered = moviesToFilter.filter { movie ->
            val categoryMatch = selectedCategories.isEmpty() || selectedCategories.contains(movie.category)
            categoryMatch
        }
          // Apply sorting based on year selection
        val yearSorted = when (selectedYear) {
            "All" -> filtered
            "Newest First" -> filtered.sortedByDescending { it.releaseYear }
            "Oldest First" -> filtered.sortedBy { it.releaseYear }
            else -> filtered
        }
        
        // Apply sorting based on rating selection
        when (selectedRating) {
            "All" -> yearSorted
            "High to Low" -> yearSorted.sortedByDescending { it.rating }
            "Low to High" -> yearSorted.sortedBy { it.rating }
            else -> yearSorted
        }
    }      // Check if any filters are applied
    val hasActiveFilters = selectedCategories.isNotEmpty()
    
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
                    Box {
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
                        
                        // Filter Dropdown Menu
                        DropdownMenu(
                            expanded = showFilters,
                            onDismissRequest = { showFilters = false },
                            modifier = Modifier
                                .width(320.dp)
                                .background(Color(0xFF1A1A2E))
                        ) {
                            FilterDropdownContent(
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
                                },                                onClearFilters = {
                                    tempSelectedCategories = setOf()
                                    tempSelectedYear = "Newest First"
                                    tempSelectedRating = "High to Low"
                                    selectedCategories = setOf()
                                    selectedYear = "Newest First"
                                    selectedRating = "High to Low"
                                },
                                categories = movieViewModel.getMovieCategories()
                            )
                        }
                    }
                },colors = TopAppBarDefaults.topAppBarColors(
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
                Column {
                    // Content Section
                    if (searchQuery.isEmpty() && !hasActiveFilters) {                        // Search prompt (no search and no filters)
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
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
                    } else if (filteredResults.isEmpty()) {                        // No results
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
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
                            ) {                                  // Show active filters
                                if (hasActiveFilters) {
                                    val activeFilters = mutableListOf<String>()
                                    if (selectedCategories.isNotEmpty()) {
                                        activeFilters.add("Categories: ${selectedCategories.joinToString(", ")}")
                                    }
                                    activeFilters.add("Year: $selectedYear")
                                    activeFilters.add("Rating: $selectedRating")
                                    
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
fun FilterDropdownContent(
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
    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var yearDropdownExpanded by remember { mutableStateOf(false) }
    var ratingDropdownExpanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filters",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )              // Clear filters button
            if (selectedCategories.isNotEmpty()) {
                TextButton(
                    onClick = onClearFilters,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White.copy(alpha = 0.8f)
                    )
                ) {
                    Text(
                        text = "Clear All",
                        fontSize = 11.sp
                    )
                }
            }
        }        
        // Category Dropdown Filter
        Column(modifier = Modifier.padding(bottom = 6.dp)) {
            Text(
                text = "Categories",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 3.dp)
            )
            
            Box {
                OutlinedButton(
                    onClick = { categoryDropdownExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedCategories.isEmpty()) "Categories" 
                                   else "${selectedCategories.size} selected",
                            fontSize = 12.sp
                        )
                        Icon(
                            imageVector = if (categoryDropdownExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
                DropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false },
                    modifier = Modifier
                        .width(280.dp)
                        .background(Color(0xFF2A2A3E))
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedCategories.contains(category),
                                        onCheckedChange = {
                                            val newCategories = if (selectedCategories.contains(category)) {
                                                selectedCategories - category
                                            } else {
                                                selectedCategories + category
                                            }
                                            onCategoriesChange(newCategories)
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Color(0xFF4CAF50),
                                            uncheckedColor = Color.White
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = category,
                                        color = Color.White,
                                        fontSize = 13.sp
                                    )
                                }
                            },
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
            }
        }          // Year Dropdown Filter
        Column(modifier = Modifier.padding(bottom = 6.dp)) {
            Text(
                text = "Year",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 3.dp)
            )
            
            val years = listOf("All", "Newest First", "Oldest First")
            
            Box {
                OutlinedButton(
                    onClick = { yearDropdownExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedYear,
                            fontSize = 12.sp
                        )
                        Icon(
                            imageVector = if (yearDropdownExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
                DropdownMenu(
                    expanded = yearDropdownExpanded,
                    onDismissRequest = { yearDropdownExpanded = false },
                    modifier = Modifier
                        .width(280.dp)
                        .background(Color(0xFF2A2A3E))
                ) {
                    years.forEach { year ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = year,
                                    color = if (selectedYear == year) Color(0xFF4CAF50) else Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedYear == year) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onYearChange(year)
                                yearDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }
        // Rating Dropdown Filter
        Column(modifier = Modifier.padding(bottom = 10.dp)) {
            Text(
                text = "Rating",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 3.dp)
            )
            
            val ratings = listOf("All", "High to Low", "Low to High")
            
            Box {
                OutlinedButton(
                    onClick = { ratingDropdownExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedRating,
                            fontSize = 12.sp
                        )
                        Icon(
                            imageVector = if (ratingDropdownExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
                DropdownMenu(
                    expanded = ratingDropdownExpanded,
                    onDismissRequest = { ratingDropdownExpanded = false },
                    modifier = Modifier
                        .width(280.dp)
                        .background(Color(0xFF2A2A3E))
                ) {
                    ratings.forEach { rating ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = rating,
                                    color = if (selectedRating == rating) Color(0xFF4CAF50) else Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedRating == rating) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onRatingChange(rating)
                                ratingDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }
          // Apply Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onApplyFilters,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                modifier = Modifier.fillMaxWidth(0.5f),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Text("Apply", fontSize = 12.sp)
            }
        }
    }
}


