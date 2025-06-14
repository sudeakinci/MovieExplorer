package com.example.moviemobileproject.data.repository

import com.example.moviemobileproject.data.model.Movie
import com.example.moviemobileproject.data.model.MovieDetails
import com.example.moviemobileproject.data.model.SavedMovie
import com.example.moviemobileproject.data.model.TmdbCastMember
import com.example.moviemobileproject.data.model.PersonDetails
import com.example.moviemobileproject.data.model.PersonMovie
import com.example.moviemobileproject.data.model.toMovie
import com.example.moviemobileproject.data.model.toMovieDetails
import com.example.moviemobileproject.data.model.toPersonDetails
import com.example.moviemobileproject.data.model.toPersonMovie
import com.example.moviemobileproject.data.network.NetworkModule
import com.example.moviemobileproject.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

@Singleton
class MovieRepository {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val tmdbApi = NetworkModule.tmdbApi
    suspend fun getPopularMovies(): Result<List<Movie>> {
        return try {
            // First try to get from TMDB API
            val tmdbResponse = tmdbApi.getPopularMovies(NetworkModule.TMDB_API_KEY)
            if (tmdbResponse.isSuccessful) {
                val tmdbMovies = tmdbResponse.body()?.results ?: emptyList()
                val movies = tmdbMovies.map { it.toMovie() }
                Result.success(movies)
            } else {
                // Fall back to Firebase/sample data
                val snapshot = firestore.collection("movies")
                    .whereEqualTo("isPopular", true)
                    .get()
                    .await()
                
                val movies = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Movie::class.java)?.copy(id = doc.id)
                }
                Result.success(movies)
            }
        } catch (e: Exception) {
            // Final fallback to sample data
            Result.success(getSampleMovies().filter { it.isPopular })
        }
    }    suspend fun getMoviesByCategory(category: String): Result<List<Movie>> {
        return try {
            // Try to get from TMDB API based on category
            val genreId = getCategoryGenreId(category)
            val tmdbResponse = tmdbApi.getMoviesByGenre(NetworkModule.TMDB_API_KEY, genreId.toString())
            
            if (tmdbResponse.isSuccessful) {
                val tmdbMovies = tmdbResponse.body()?.results ?: emptyList()
                val movies = tmdbMovies.map { it.toMovie() }
                Result.success(movies)
            } else {
                // Fall back to Firebase
                val snapshot = firestore.collection("movies")
                    .whereEqualTo("category", category)
                    .get()
                    .await()
                
                val movies = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Movie::class.java)?.copy(id = doc.id)
                }
                Result.success(movies)
            }
        } catch (e: Exception) {
            Result.success(getSampleMovies().filter { it.category == category })
        }    }
      
    private fun getCategoryGenreId(category: String): Int {
        return Constants.GENRE_MAPPING[category] ?: 18 // Default to Drama
    }
    
    suspend fun searchMovies(query: String): Result<List<Movie>> {
        return try {
            // Try TMDB API first
            val tmdbResponse = tmdbApi.searchMovies(NetworkModule.TMDB_API_KEY, query)
            
            if (tmdbResponse.isSuccessful) {
                val tmdbMovies = tmdbResponse.body()?.results ?: emptyList()
                val movies = tmdbMovies.map { it.toMovie() }
                Result.success(movies)
            } else {
                // Fall back to Firebase
                val snapshot = firestore.collection("movies")
                    .get()
                    .await()
                
                val movies = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Movie::class.java)?.copy(id = doc.id)
                }.filter { 
                    it.title.contains(query, ignoreCase = true) 
                }
                Result.success(movies)
            }
        } catch (e: Exception) {
            Result.success(getSampleMovies().filter { 
                it.title.contains(query, ignoreCase = true) 
            })
        }
    }    suspend fun getAllMovies(): Result<List<Movie>> {
        return try {
            // Try to get from TMDB API (popular movies as a good sample)
            val tmdbResponse = tmdbApi.getPopularMovies(NetworkModule.TMDB_API_KEY)
            if (tmdbResponse.isSuccessful) {
                val tmdbMovies = tmdbResponse.body()?.results ?: emptyList()
                val movies = tmdbMovies.map { it.toMovie() }
                Result.success(movies)
            } else {
                // Fall back to Firebase
                val snapshot = firestore.collection("movies")
                    .get()
                    .await()
                
                val movies = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Movie::class.java)?.copy(id = doc.id)
                }
                Result.success(movies)
            }
        } catch (e: Exception) {
            Result.success(getSampleMovies())
        }
    }
      suspend fun getSavedMovies(): Result<List<SavedMovie>> {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            return try {
                val snapshot = firestore.collection("users")
                    .document(userId)
                    .get()
                    .await()                
                    val savedMovies = snapshot.get("saved_movies") as? List<HashMap<String, Any>> ?: emptyList()
                val movies = savedMovies.map { map ->
                    SavedMovie(
                        movieId = map["movieId"] as? String ?: "",
                        title = map["title"] as? String ?: "",
                        imageUrl = map["imageUrl"] as? String ?: "",
                        category = map["category"] as? String ?: "",
                        description = map["description"] as? String ?: "",
                        savedAt = (map["savedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
                    )
                }
                Result.success(movies)
            } catch (e: Exception) {
                Result.success(emptyList())
            }
        } else {
            return Result.success(emptyList())
        }
    }
      suspend fun saveMovie(movie: Movie): Result<Unit> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
          return try {
            val savedMovie = SavedMovie(
                movieId = movie.id,
                title = movie.title,
                imageUrl = movie.imageUrl,
                category = movie.category,
                description = movie.description
            )
            
            firestore.collection("users")
                .document(userId)
                .update("saved_movies", com.google.firebase.firestore.FieldValue.arrayUnion(savedMovie))
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun removeSavedMovie(movieId: String): Result<Unit> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
        
        return try {
            val userDoc = firestore.collection("users").document(userId).get().await()
            val savedMovies = userDoc.get("saved_movies") as? List<HashMap<String, Any>> ?: emptyList()
            
            val updatedMovies = savedMovies.filter { map ->
                map["movieId"] as? String != movieId
            }
            
            firestore.collection("users")
                .document(userId)
                .update("saved_movies", updatedMovies)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun getSampleMovies(): List<Movie> {
        return listOf(
            Movie(
                id = "1",
                title = "Inception",
                category = "Action",
                isPopular = true,
                imageUrl = "https://image.tmdb.org/t/p/w500/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg",
                description = "A skilled thief who steals corporate secrets through dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
                rating = 8.8,
                releaseYear = 2010
            ),
            Movie(
                id = "2",
                title = "The Dark Knight",
                category = "Action",
                isPopular = true,
                imageUrl = "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
                description = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
                rating = 9.0,
                releaseYear = 2008
            ),
            Movie(
                id = "3",
                title = "Interstellar",
                category = "Sci-Fi",
                isPopular = true,
                imageUrl = "https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg",
                description = "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.",
                rating = 8.6,
                releaseYear = 2014
            ),
            Movie(
                id = "4",
                title = "The Godfather",
                category = "Drama",
                isPopular = true,
                imageUrl = "https://image.tmdb.org/t/p/w500/3bhkrj58Vtu7enYsRolD1fZdja1.jpg",
                description = "An organized crime dynasty's aging patriarch transfers control of his clandestine empire to his reluctant son.",
                rating = 9.2,
                releaseYear = 1972
            ),
            Movie(
                id = "5",
                title = "Pulp Fiction",
                category = "Crime",
                isPopular = false,
                imageUrl = "https://image.tmdb.org/t/p/w500/d5iIlFn5s0ImszYzBPb8JPIfbXD.jpg",
                description = "The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.",
                rating = 8.9,
                releaseYear = 1994
            ),
            Movie(
                id = "6",
                title = "Avatar",
                category = "Sci-Fi",
                isPopular = true,
                imageUrl = "https://image.tmdb.org/t/p/w500/jRXYjXNq0Cs2TcJjLkki24MLp7u.jpg",
                description = "A paraplegic Marine dispatched to the moon Pandora on a unique mission becomes torn between following his orders and protecting the world he feels is his home.",
                rating = 7.8,
                releaseYear = 2009
            ),
            Movie(
                id = "7",
                title = "The Shawshank Redemption",
                category = "Drama",
                isPopular = true,
                imageUrl = "https://image.tmdb.org/t/p/w500/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg",
                description = "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
                rating = 9.3,
                releaseYear = 1994
            ),
            Movie(
                id = "8",
                title = "Forrest Gump",
                category = "Drama",
                isPopular = false,
                imageUrl = "https://image.tmdb.org/t/p/w500/arw2vcBveWOVZr6pxd9XTd1TdQa.jpg",
                description = "The presidencies of Kennedy and Johnson, the Vietnam War, the Watergate scandal and other historical events unfold from the perspective of an Alabama man with an IQ of 75.",
                rating = 8.8,
                releaseYear = 1994
            ),
            Movie(
                id = "9",
                title = "Titanic",
                category = "Romance",
                isPopular = false,
                imageUrl = "https://image.tmdb.org/t/p/w500/9xjZS2rlVxm8SFx8kPC3aIGCOYQ.jpg",
                description = "A seventeen-year-old aristocrat falls in love with a kind but poor artist aboard the luxurious, ill-fated R.M.S. Titanic.",
                rating = 7.8,
                releaseYear = 1997
            ),
            Movie(
                id = "10",
                title = "The Matrix",
                category = "Sci-Fi",
                isPopular = true,
                imageUrl = "https://image.tmdb.org/t/p/w500/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg",
                description = "A computer programmer is led to fight an underground war against powerful computers who have constructed his entire reality with a system called the Matrix.",
                rating = 8.7,                releaseYear = 1999
            )
        )
    }
    
    fun getMovieCategories(): List<String> {
        return listOf(
            "Action",
            "Comedy", 
            "Drama",
            "Horror",
            "Romance",
            "Sci-Fi",
            "Thriller",
            "Animation",
            "Adventure",
            "Crime"
        )
    }
    
    suspend fun getMovieDetails(movieId: String): Result<MovieDetails> {
        return try {
            val movieIdInt = movieId.toIntOrNull() ?: return Result.failure(Exception("Invalid movie ID"))
            
            // Get movie details
            val detailsResponse = tmdbApi.getMovieDetails(movieIdInt, NetworkModule.TMDB_API_KEY)
            if (!detailsResponse.isSuccessful) {
                return Result.failure(Exception("Failed to fetch movie details"))
            }
            
            val movieDetails = detailsResponse.body() ?: return Result.failure(Exception("Empty response"))
            
            // Get movie videos (trailers)
            val videosResponse = tmdbApi.getMovieVideos(movieIdInt, NetworkModule.TMDB_API_KEY)
            val trailerKey = videosResponse.body()?.results
                ?.firstOrNull { it.type == "Trailer" && it.site == "YouTube" }?.key
              // Get movie credits (cast)
            val creditsResponse = tmdbApi.getMovieCredits(movieIdInt, NetworkModule.TMDB_API_KEY)
            val tmdbCast = creditsResponse.body()?.cast ?: emptyList()
            
            // Convert TmdbCast to TmdbCastMember
            val cast = tmdbCast.map { tmdbCastItem ->
                TmdbCastMember(
                    id = tmdbCastItem.id,
                    name = tmdbCastItem.name,
                    character = tmdbCastItem.character,
                    profilePath = tmdbCastItem.profilePath,
                    order = tmdbCastItem.order
                )
            }
            
            // Convert to app model
            val details = movieDetails.toMovieDetails(trailerKey, cast)
            Result.success(details)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPersonDetails(personId: String): Result<PersonDetails> {
        return try {
            val personIdInt = personId.toIntOrNull() ?: return Result.failure(Exception("Invalid person ID"))
            
            val response = tmdbApi.getPersonDetails(personIdInt, NetworkModule.TMDB_API_KEY)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Failed to fetch person details"))
            }
            
            val personDetails = response.body() ?: return Result.failure(Exception("Empty response"))
            Result.success(personDetails.toPersonDetails())
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPersonMovieCredits(personId: String): Result<List<PersonMovie>> {
        return try {
            val personIdInt = personId.toIntOrNull() ?: return Result.failure(Exception("Invalid person ID"))
            
            val response = tmdbApi.getPersonMovieCredits(personIdInt, NetworkModule.TMDB_API_KEY)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Failed to fetch person movie credits"))
            }
            
            val credits = response.body() ?: return Result.failure(Exception("Empty response"))
            
            // Combine cast and crew movies, prioritize cast roles
            val castMovies = credits.cast.map { it.toPersonMovie() }
            val crewMovies = credits.crew.map { it.toPersonMovie() }
            
            // Sort by release date (most recent first) and take top 20
            val allMovies = (castMovies + crewMovies)
                .sortedByDescending { it.releaseDate }
                .take(20)
            
            Result.success(allMovies)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
