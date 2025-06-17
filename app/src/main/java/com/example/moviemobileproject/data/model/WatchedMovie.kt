package com.example.moviemobileproject.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WatchedMovie(
    val movieId: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val description: String = "",
    val rating: Double = 0.0,
    val releaseYear: Int = 0,
    val watchedAt: Long = System.currentTimeMillis()
) : Parcelable
