package com.example.moviemobileproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviemobileproject.data.model.Movie
import com.example.moviemobileproject.data.model.SavedMovie
import com.example.moviemobileproject.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {
    
    private val movieRepository = MovieRepository()
    
    private val _popularMovies = MutableStateFlow<List<Movie>>(emptyList())
    val popularMovies: StateFlow<List<Movie>> = _popularMovies
    
    private val _categoryMovies = MutableStateFlow<List<Movie>>(emptyList())
    val categoryMovies: StateFlow<List<Movie>> = _categoryMovies
    
    private val _searchResults = MutableStateFlow<List<Movie>>(emptyList())
    val searchResults: StateFlow<List<Movie>> = _searchResults
    
    private val _savedMovies = MutableStateFlow<List<SavedMovie>>(emptyList())
    val savedMovies: StateFlow<List<SavedMovie>> = _savedMovies
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    init {
        loadPopularMovies()
        loadSavedMovies()
    }
    
    fun loadPopularMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            movieRepository.getPopularMovies().collect { movies ->
                _popularMovies.value = movies
                _isLoading.value = false
            }
        }
    }
    
    fun loadMoviesByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            movieRepository.getMoviesByCategory(category).collect { movies ->
                _categoryMovies.value = movies
                _isLoading.value = false
            }
        }
    }
    
    fun searchMovies(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            movieRepository.searchMovies(query).collect { movies ->
                _searchResults.value = movies
                _isLoading.value = false
            }
        }
    }
    
    fun loadSavedMovies() {
        viewModelScope.launch {
            movieRepository.getSavedMovies().collect { movies ->
                _savedMovies.value = movies
            }
        }
    }
    
    fun saveMovie(movie: Movie) {
        viewModelScope.launch {
            movieRepository.saveMovie(movie)
                .onSuccess {
                    loadSavedMovies() // Refresh saved movies
                }
                .onFailure {
                    _errorMessage.value = "Failed to save movie: ${it.message}"
                }
        }
    }
    
    fun removeSavedMovie(movieId: String) {
        viewModelScope.launch {
            movieRepository.removeSavedMovie(movieId)
                .onSuccess {
                    loadSavedMovies() // Refresh saved movies
                }
                .onFailure {
                    _errorMessage.value = "Failed to remove movie: ${it.message}"
                }
        }
    }
    
    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun getCategories(): List<String> {
        return listOf("Action", "Drama", "Sci-Fi", "Crime", "Romance", "Comedy", "Horror", "Thriller")
    }
}
