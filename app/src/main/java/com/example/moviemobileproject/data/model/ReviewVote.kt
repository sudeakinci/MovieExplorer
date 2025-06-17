package com.example.moviemobileproject.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReviewVote(
    val id: String = "",
    val reviewId: String = "",
    val userId: String = "",
    val voteType: VoteType = VoteType.NONE, // LIKE, DISLIKE, or NONE
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable

enum class VoteType {
    NONE, LIKE, DISLIKE
}
