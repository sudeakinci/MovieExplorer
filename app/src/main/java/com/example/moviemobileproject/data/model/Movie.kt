package com.example.moviemobileproject.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val isPopular: Boolean = false,
    val imageUrl: String = "",
    val description: String = "",
    val rating: Double = 0.0,
    val releaseYear: Int = 0,
    val director: String = "",
    val cast: List<String> = emptyList(),
    val genre: List<String> = emptyList(),
    val duration: Int = 0 // minutes
) : Parcelable


data class MovieCategory(
    val id: String = "",
    val name: String = "",
)
