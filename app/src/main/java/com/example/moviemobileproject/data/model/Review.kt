package com.example.moviemobileproject.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(
    val id: String = "",
    val movieId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val likes: Int = 0,
    val dislikes: Int = 0
) : Parcelable
