package com.example.moviemobileproject.utils

import com.example.moviemobileproject.data.model.Movie
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseDataInitializer {
    
    private val firestore = FirebaseFirestore.getInstance()
    
    suspend fun initializeSampleData() {
        try {
            // Check if data already exists
            val snapshot = firestore.collection("movies").limit(1).get().await()
            if (!snapshot.isEmpty) {
                return // Data already exists
            }
            
            // Sample movies data
            val sampleMovies = listOf(
                Movie(
                    title = "Inception",
                    category = "Sci-Fi",
                    isPopular = true,
                    imageUrl = "https://images.unsplash.com/photo-1489599510739-9c4e0f5cf7d5?w=400",
                    description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
                    rating = 8.8,
                    releaseYear = 2010,
                    director = "Christopher Nolan",
                    genre = listOf("Sci-Fi", "Action", "Thriller"),
                    duration = 148
                ),
                Movie(
                    title = "The Dark Knight",
                    category = "Action",
                    isPopular = true,
                    imageUrl = "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400",
                    description = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
                    rating = 9.0,
                    releaseYear = 2008,
                    director = "Christopher Nolan",
                    genre = listOf("Action", "Crime", "Drama"),
                    duration = 152
                ),
                Movie(
                    title = "Pulp Fiction",
                    category = "Crime",
                    isPopular = true,
                    imageUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=400",
                    description = "The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.",
                    rating = 8.9,
                    releaseYear = 1994,
                    director = "Quentin Tarantino",
                    genre = listOf("Crime", "Drama"),
                    duration = 154
                ),
                Movie(
                    title = "The Shawshank Redemption",
                    category = "Drama",
                    isPopular = true,
                    imageUrl = "https://images.unsplash.com/photo-1489599510739-9c4e0f5cf7d5?w=400",
                    description = "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
                    rating = 9.3,
                    releaseYear = 1994,
                    director = "Frank Darabont",
                    genre = listOf("Drama"),
                    duration = 142
                ),
                Movie(
                    title = "Toy Story",
                    category = "Animation",
                    isPopular = true,
                    imageUrl = "https://images.unsplash.com/photo-1606041011872-596597976b25?w=400",
                    description = "A cowboy doll is profoundly threatened and jealous when a new spaceman figure supplants him as top toy in a boy's room.",
                    rating = 8.3,
                    releaseYear = 1995,
                    director = "John Lasseter",
                    genre = listOf("Animation", "Adventure", "Comedy"),
                    duration = 81
                ),
                Movie(
                    title = "The Avengers",
                    category = "Action",
                    isPopular = true,
                    imageUrl = "https://images.unsplash.com/photo-1635863138275-d9864d99c6ef?w=400",
                    description = "Earth's mightiest heroes must come together and learn to fight as a team if they are going to stop the mischievous Loki and his alien army from enslaving humanity.",
                    rating = 8.0,
                    releaseYear = 2012,
                    director = "Joss Whedon",
                    genre = listOf("Action", "Adventure", "Sci-Fi"),
                    duration = 143
                ),
                Movie(
                    title = "Titanic",
                    category = "Romance",
                    isPopular = true,
                    imageUrl = "https://images.unsplash.com/photo-1544966503-7cc5ac882d5f?w=400",
                    description = "A seventeen-year-old aristocrat falls in love with a kind but poor artist aboard the luxurious, ill-fated R.M.S. Titanic.",
                    rating = 7.8,
                    releaseYear = 1997,
                    director = "James Cameron",
                    genre = listOf("Romance", "Drama"),
                    duration = 194
                ),
                Movie(
                    title = "The Conjuring",
                    category = "Horror",
                    isPopular = false,
                    imageUrl = "https://images.unsplash.com/photo-1509248961158-e54f6934749c?w=400",
                    description = "Paranormal investigators Ed and Lorraine Warren work to help a family terrorized by a dark presence in their farmhouse.",
                    rating = 7.5,
                    releaseYear = 2013,
                    director = "James Wan",
                    genre = listOf("Horror", "Mystery", "Thriller"),
                    duration = 112
                ),
                Movie(
                    title = "Superbad",
                    category = "Comedy",
                    isPopular = false,
                    imageUrl = "https://images.unsplash.com/photo-1489599510739-9c4e0f5cf7d5?w=400",
                    description = "Two co-dependent high school seniors are forced to deal with separation anxiety after their plan to stage a booze-soaked party goes awry.",
                    rating = 7.6,
                    releaseYear = 2007,
                    director = "Greg Mottola",
                    genre = listOf("Comedy"),
                    duration = 113
                ),
                Movie(
                    title = "Casino Royale",
                    category = "Thriller",
                    isPopular = false,
                    imageUrl = "https://images.unsplash.com/photo-1489599510739-9c4e0f5cf7d5?w=400",
                    description = "After receiving a license to kill, Secret Agent James Bond sets out on his first mission as 007.",
                    rating = 8.0,
                    releaseYear = 2006,
                    director = "Martin Campbell",
                    genre = listOf("Action", "Adventure", "Thriller"),
                    duration = 144
                )
            )
            
            // Add movies to Firestore
            sampleMovies.forEach { movie ->
                firestore.collection("movies")
                    .add(movie)
                    .await()
            }
            
            println("Sample data initialized successfully!")
            
        } catch (e: Exception) {
            println("Error initializing sample data: ${e.message}")
        }
    }
}
