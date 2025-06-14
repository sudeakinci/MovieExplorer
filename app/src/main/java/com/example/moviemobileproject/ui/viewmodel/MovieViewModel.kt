package com.example.moviemobileproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviemobileproject.data.model.Movie
import com.example.moviemobileproject.data.model.SavedMovie
import com.example.moviemobileproject.data.model.MovieDetails
import com.example.moviemobileproject.data.model.PersonDetails
import com.example.moviemobileproject.data.model.PersonMovie
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
    
    private val _allMovies = MutableStateFlow<List<Movie>>(emptyList())
    val allMovies: StateFlow<List<Movie>> = _allMovies
    
    private val _movieDetails = MutableStateFlow<MovieDetails?>(null)
    val movieDetails: StateFlow<MovieDetails?> = _movieDetails
    
    private val _personDetails = MutableStateFlow<PersonDetails?>(null)
    val personDetails: StateFlow<PersonDetails?> = _personDetails
    
    private val _personMovies = MutableStateFlow<List<PersonMovie>>(emptyList())
    val personMovies: StateFlow<List<PersonMovie>> = _personMovies
    
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
            movieRepository.getPopularMovies()
                .onSuccess { movies ->
                    _popularMovies.value = movies
                    _errorMessage.value = null
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message
                }
            _isLoading.value = false
        }
    }
    
    fun loadMoviesByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            movieRepository.getMoviesByCategory(category)
                .onSuccess { movies ->
                    _categoryMovies.value = movies
                    _errorMessage.value = null
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message
                }
            _isLoading.value = false
        }
    }
    
    fun searchMovies(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            movieRepository.searchMovies(query)
                .onSuccess { movies ->
                    _searchResults.value = movies
                    _errorMessage.value = null
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message
                }
            _isLoading.value = false
        }
    }
    
    fun loadSavedMovies() {
        viewModelScope.launch {
            movieRepository.getSavedMovies()
                .onSuccess { movies ->
                    _savedMovies.value = movies
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message
                }
        }
    }
    
    fun saveMovie(movie: Movie) {
        viewModelScope.launch {
            movieRepository.saveMovie(movie)
                .onSuccess {
                    loadSavedMovies() // Refresh saved movies
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message
                }
        }
    }
    
    fun removeSavedMovie(movieId: String) {
        viewModelScope.launch {
            movieRepository.removeSavedMovie(movieId)
                .onSuccess {
                    loadSavedMovies() // Refresh saved movies
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message
                }
        }
    }
    
    fun loadAllMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            movieRepository.getAllMovies()
                .onSuccess { movies ->
                    _allMovies.value = movies
                    _errorMessage.value = null
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message
                }
            _isLoading.value = false
        }
    }
    
    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun loadMovieDetails(movieId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            movieRepository.getMovieDetails(movieId)
                .onSuccess { details ->
                    _movieDetails.value = details
                    _errorMessage.value = null
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message
                }
            _isLoading.value = false
        }
    }
    
    fun clearMovieDetails() {
        _movieDetails.value = null
    }
    
    fun loadPersonDetails(personId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            movieRepository.getPersonDetails(personId)
                .onSuccess { details ->
                    _personDetails.value = details
                    _errorMessage.value = null
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message
                }
            _isLoading.value = false
        }
    }
    
    fun loadPersonMovieCredits(personId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            movieRepository.getPersonMovieCredits(personId)
                .onSuccess { movies ->
                    _personMovies.value = movies
                    _errorMessage.value = null
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message
                }
            _isLoading.value = false
        }
    }
    
    fun clearPersonDetails() {
        _personDetails.value = null
        _personMovies.value = emptyList()
    }
    
    fun getMovieCategories(): List<String> {
        return movieRepository.getMovieCategories()
    }
}
