package com.example.moviemobileproject.utils

object Constants {
    
    // TMDB API Configuration
    const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
    
    // Replace this with your actual TMDB API key
    // Get your API key from: https://www.themoviedb.org/settings/api
    const val TMDB_API_KEY = "539e809d39ce19b3ba0a9a6338e99d98"
    
    // Genre mapping for categories
    val GENRE_MAPPING = mapOf(
        "Action" to 28,
        "Comedy" to 35,
        "Drama" to 18,
        "Horror" to 27,
        "Romance" to 10749,
        "Sci-Fi" to 878,
        "Thriller" to 53,
        "Animation" to 16,
        "Adventure" to 12,
        "Crime" to 80
    )
    
    val REVERSE_GENRE_MAPPING = GENRE_MAPPING.entries.associate { (k, v) -> v to k }
    
    // Error messages
    const val NETWORK_ERROR_MESSAGE = "Network error. Please check your internet connection."
    const val API_ERROR_MESSAGE = "Unable to fetch movies from server."
    const val AUTH_ERROR_MESSAGE = "Please login to save movies."
}
