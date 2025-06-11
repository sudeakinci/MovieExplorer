package com.example.moviemobileproject.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SavedMovie(
    val movieId: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val description: String = "",
    val savedAt: Long = System.currentTimeMillis()
) : Parcelable
