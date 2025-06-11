package com.example.moviemobileproject.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val profileImageUrl: String = "",
    val savedMovies: List<SavedMovie> = emptyList()
)
