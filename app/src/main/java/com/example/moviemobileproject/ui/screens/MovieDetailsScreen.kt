package com.example.moviemobileproject.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moviemobileproject.ui.components.ReviewSection
import com.example.moviemobileproject.ui.components.AddReviewDialog
import com.example.moviemobileproject.data.model.CastMember
import com.example.moviemobileproject.data.model.MovieDetails
import com.example.moviemobileproject.ui.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    navController: NavController,
    movieId: String,
    movieViewModel: MovieViewModel = viewModel()
) {    val movieDetails by movieViewModel.movieDetails.collectAsState()
    val reviews by movieViewModel.movieReviews.collectAsState()
    val isLoading by movieViewModel.isLoading.collectAsState()
    val errorMessage by movieViewModel.errorMessage.collectAsState()
    val savedMovies by movieViewModel.savedMovies.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(movieId) {
        println("DEBUG: MovieDetailsScreen loading for movieId: '$movieId'")
        movieViewModel.loadMovieDetails(movieId)
        movieViewModel.loadMovieReviews(movieId)
    }
    
    // Clear movie details when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            movieViewModel.clearMovieDetails()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = movieDetails?.title ?: "Movie Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                
                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "❌",
                                fontSize = 48.sp
                            )
                            Text(
                                text = "Error loading movie details",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = errorMessage ?: "Unknown error",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    }
                }
                  movieDetails != null -> {                    MovieDetailsContent(
                        movieDetails = movieDetails!!,
                        reviews = reviews,
                        isSaved = savedMovies.any { it.movieId == movieId },
                        onSaveClick = {
                            if (savedMovies.any { it.movieId == movieId }) {
                                movieViewModel.removeSavedMovie(movieId)
                            } else {
                                // Create a complete Movie object for saving with all details
                                val releaseYear = if (movieDetails!!.releaseDate.isNotEmpty()) {
                                    try {
                                        movieDetails!!.releaseDate.substring(0, 4).toInt()
                                    } catch (e: Exception) { 0 }
                                } else { 0 }
                                
                                val movie = com.example.moviemobileproject.data.model.Movie(
                                    id = movieId,
                                    title = movieDetails!!.title,
                                    imageUrl = movieDetails!!.posterUrl,
                                    category = movieDetails!!.genres.firstOrNull() ?: "Unknown",
                                    description = movieDetails!!.overview,
                                    rating = movieDetails!!.rating,
                                    releaseYear = releaseYear,
                                    duration = movieDetails!!.runtime,
                                    genre = movieDetails!!.genres,
                                    director = "", // Director info not available in current MovieDetails model
                                    cast = movieDetails!!.cast.map { it.name }
                                )
                                movieViewModel.saveMovie(movie)
                            }
                        },                        onTrailerClick = { trailerUrl ->
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Handle error - could show a toast or snackbar
                            }
                        },
                        onAddReview = { rating, comment ->
                            movieViewModel.addReview(movieId, rating, comment)
                        },
                        onLikeReview = { reviewId ->
                            movieViewModel.likeReview(reviewId)
                        },
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun MovieDetailsContent(
    movieDetails: MovieDetails,
    reviews: List<com.example.moviemobileproject.data.model.Review>,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onTrailerClick: (String) -> Unit,
    onAddReview: (Float, String) -> Unit,
    onLikeReview: (String) -> Unit,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(0.dp)
    ) {
        item {
            // Movie Poster and Backdrop
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            ) {
                // Backdrop Image
                AsyncImage(
                    model = movieDetails.backdropUrl.ifEmpty { movieDetails.posterUrl },
                    contentDescription = movieDetails.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Black.copy(alpha = 0.8f)
                                ),
                                startY = 100f
                            )
                        )
                )
                
                // Play Button (if trailer available)
                if (movieDetails.trailerUrl != null) {
                    FloatingActionButton(
                        onClick = { onTrailerClick(movieDetails.trailerUrl!!) },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(72.dp),
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Play Trailer",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                
                // Save Button
                IconButton(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            CircleShape
                        )
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isSaved) "Remove from favorites" else "Add to favorites",
                        tint = if (isSaved) Color.Red else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // Movie info overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                        .fillMaxWidth(0.8f)
                ) {
                    Text(
                        text = movieDetails.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (movieDetails.tagline?.isNotEmpty() == true) {
                        Text(
                            text = movieDetails.tagline!!,
                            fontSize = 16.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
        
        item {
            // Movie Information
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Rating and Runtime
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color.Yellow,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", movieDetails.rating),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "/10",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    
                    // Runtime
                    if (movieDetails.runtime > 0) {
                        Text(
                            text = "${movieDetails.runtime} min",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Genres
                if (movieDetails.genres.isNotEmpty()) {
                    Text(
                        text = "Genres",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(movieDetails.genres) { genre ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.1f)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = genre,
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
                
                // Release Date
                if (movieDetails.releaseDate.isNotEmpty()) {
                    Text(
                        text = "Release Date",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = movieDetails.releaseDate,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                
                // Overview
                if (movieDetails.overview.isNotEmpty()) {
                    Text(
                        text = "Overview",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = movieDetails.overview,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
            }
        }
        
        // Cast Section
        if (movieDetails.cast.isNotEmpty()) {
            item {
                Text(
                    text = "Cast",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                  LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(movieDetails.cast) { castMember ->
                        CastMemberCard(
                            castMember = castMember,
                            onClick = {
                                navController.navigate("person_details/${castMember.id}")
                            }
                        )
                    }                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        
        // Reviews Section
        item {
            var showAddReviewDialog by remember { mutableStateOf(false) }
            
            ReviewSection(
                reviews = reviews,
                onAddReviewClick = { showAddReviewDialog = true },
                onLikeReview = onLikeReview,
                modifier = Modifier.padding(16.dp)
            )
            
            // Add Review Dialog
            if (showAddReviewDialog) {
                AddReviewDialog(
                    onDismiss = { showAddReviewDialog = false },
                    onSubmit = { rating, comment ->
                        onAddReview(rating, comment)
                        showAddReviewDialog = false
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun CastMemberCard(
    castMember: CastMember,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {            // Actor Image
            AsyncImage(
                model = castMember.profilePath ?: "",
                contentDescription = castMember.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.3f))
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Actor Name
            Text(
                text = castMember.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            // Character Name
            Text(
                text = castMember.character,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
