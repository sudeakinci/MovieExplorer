package com.example.moviemobileproject.ui.screens

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
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.moviemobileproject.data.model.PersonDetails
import com.example.moviemobileproject.data.model.PersonMovie
import com.example.moviemobileproject.ui.components.BottomNavigationBar
import com.example.moviemobileproject.ui.viewmodel.MovieViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailsScreen(
    navController: NavController,
    personId: String,
    movieViewModel: MovieViewModel = viewModel()
) {
    val personDetails by movieViewModel.personDetails.collectAsState()
    val personMovies by movieViewModel.personMovies.collectAsState()
    val isLoading by movieViewModel.isLoading.collectAsState()
    val errorMessage by movieViewModel.errorMessage.collectAsState()
    
    LaunchedEffect(personId) {
        movieViewModel.loadPersonDetails(personId)
        movieViewModel.loadPersonMovieCredits(personId)
    }
    
    // Clear person details when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            movieViewModel.clearPersonDetails()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = personDetails?.name ?: "Person Details",
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
                                text = "Error loading person details",
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
                
                personDetails != null -> {
                    PersonDetailsContent(
                        personDetails = personDetails!!,
                        personMovies = personMovies,
                        onMovieClick = { movieId ->
                            navController.navigate("movie_details/$movieId")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PersonDetailsContent(
    personDetails: PersonDetails,
    personMovies: List<PersonMovie>,
    onMovieClick: (String) -> Unit
) {    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(0.dp)
    ) {
        item {
            // Top spacing
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item {
            // Person Header
            PersonHeaderSection(personDetails = personDetails)
        }
        
        item {
            // Biography Section
            BiographySection(biography = personDetails.biography)
        }
        
        item {
            // Spacing between biography and movies
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        item {
            // Known For / Filmography Section
            FilmographySection(
                personMovies = personMovies,
                onMovieClick = onMovieClick
            )
        }
    }
}

@Composable
fun PersonHeaderSection(personDetails: PersonDetails) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Image
            AsyncImage(
                model = personDetails.profileImageUrl,
                contentDescription = personDetails.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.3f))
            )
            
            // Person Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = personDetails.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = personDetails.knownForDepartment,
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Birthday
                if (personDetails.birthday != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Cake,
                            contentDescription = "Birthday",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formatDate(personDetails.birthday!!),
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
                
                // Place of Birth
                if (personDetails.placeOfBirth != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Place of Birth",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = personDetails.placeOfBirth!!,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                // Gender
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Gender",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = personDetails.gender,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun BiographySection(biography: String) {
    if (biography.isNotEmpty()) {
        var isExpanded by remember { mutableStateOf(false) }
        val maxLines = 15
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Biography",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = biography,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = Color.White.copy(alpha = 0.9f),
                maxLines = if (isExpanded) Int.MAX_VALUE else maxLines,
                overflow = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Show "Read More/Read Less" button only if text is long enough
            if (biography.length > 500) { // Approximate check for long text
                TextButton(
                    onClick = { isExpanded = !isExpanded },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF6366F1)
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        text = if (isExpanded) "Read Less" else "Read More",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun FilmographySection(
    personMovies: List<PersonMovie>,
    onMovieClick: (String) -> Unit
) {
    if (personMovies.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Known For",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(personMovies.take(10)) { movie ->
                    PersonMovieCard(
                        movie = movie,
                        onClick = { onMovieClick(movie.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PersonMovieCard(
    movie: PersonMovie,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(280.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
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
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 150f
                        )
                    )
            )
            
            // Movie info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = movie.title,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (movie.character != null) {
                    Text(
                        text = "as ${movie.character}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else if (movie.job != null) {
                    Text(
                        text = movie.job!!,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                if (movie.rating > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color.Yellow,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", movie.rating),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}

@Preview(showBackground = false)
@Composable
fun PersonDetailsScreenPreview() {
    val samplePersonDetails = PersonDetails(
        id = "819",
        name = "Edward Norton",
        biography = "Edward Harrison Norton is an American actor and filmmaker. He has received numerous awards and nominations, including a Golden Globe Award and three Academy Award nominations. Born in Boston, Massachusetts, and raised in Columbia, Maryland, Norton developed an interest in theatrical arts early in life.",
        birthday = "1969-08-18",
        deathday = null,
        placeOfBirth = "Boston, Massachusetts, USA",
        profileImageUrl = "https://image.tmdb.org/t/p/w500/5XBzD5WuTyVQZeS4VI25z2moMeY.jpg",
        knownForDepartment = "Acting",
        popularity = 15.654,
        gender = "Male"
    )
    
    val sampleMovies = listOf(
        PersonMovie(
            id = "550",
            title = "Fight Club",
            character = "The Narrator",
            job = null,
            department = null,
            posterUrl = "https://image.tmdb.org/t/p/w500/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
            releaseDate = "1999-10-15",
            rating = 8.4,
            overview = "A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy."
        ),
        PersonMovie(
            id = "1422",
            title = "The Departed",
            character = "Staff Sgt. Colin Sullivan",
            job = null,
            department = null,
            posterUrl = "https://image.tmdb.org/t/p/w500/tGLO9zw5ZtCeyyEWgbYGgsFxC6i.jpg",
            releaseDate = "2006-10-06",
            rating = 8.2,
            overview = "To take down South Boston's Irish Mafia, the police send in one of their own to infiltrate the underworld."
        ),
        PersonMovie(
            id = "1359",
            title = "American History X",
            character = "Derek Vinyard",
            job = null,
            department = null,
            posterUrl = "https://image.tmdb.org/t/p/w500/c2gsmSQ2Cqv8zV5hNG3ZNvFjKF0.jpg",
            releaseDate = "1998-07-01",
            rating = 8.3,
            overview = "A former neo-nazi skinhead tries to prevent his younger brother from going down the same wrong path."
        )
    )
    
    PersonDetailsContent(
        personDetails = samplePersonDetails,
        personMovies = sampleMovies,
        onMovieClick = { /* No-op for preview */ }
    )
}
