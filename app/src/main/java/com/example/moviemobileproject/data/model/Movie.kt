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
    val releaseYear: Int = 0
) : Parcelable
