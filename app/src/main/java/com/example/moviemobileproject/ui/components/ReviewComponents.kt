package com.example.moviemobileproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.moviemobileproject.data.model.Review
import com.example.moviemobileproject.data.model.VoteType
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReviewSection(
    reviews: List<Review>,
    userVotes: Map<String, VoteType>,
    onAddReviewClick: () -> Unit,
    onVoteReview: (String, VoteType) -> Unit,
    onDeleteReview: (String) -> Unit,
    currentUserId: String?,
    modifier: Modifier = Modifier
) {
    // Debug log
    println("DEBUG: ReviewSection received ${reviews.size} reviews")
    
    Column(modifier = modifier) {
        // Header with Add Review button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reviews (${reviews.size})",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Button(
                onClick = onAddReviewClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                )
            ) {
                Text(
                    text = "Add Review",
                    color = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Reviews List
        if (reviews.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {                    Text(
                        text = "",
                        fontSize = 48.sp
                    )
                    Text(
                        text = "No reviews yet",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Be the first to share your thoughts!",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(400.dp)
            ) {                items(reviews) { review ->
                    ReviewCard(
                        review = review,
                        userVote = userVotes[review.id] ?: VoteType.NONE,
                        onVote = { voteType -> onVoteReview(review.id, voteType) },
                        onDelete = { onDeleteReview(review.id) },
                        isOwnReview = currentUserId == review.userId
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewCard(
    review: Review,
    userVote: VoteType,
    onVote: (VoteType) -> Unit,
    onDelete: () -> Unit,
    isOwnReview: Boolean
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {            // User info and rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = review.userName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            .format(Date(review.timestamp)),
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Rating stars
                    Row {
                        repeat(5) { index ->
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < review.rating.toInt()) Color.Yellow else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = " ${review.rating}/5",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    
                    // Delete button - only show for user's own reviews
                    if (isOwnReview) {
                        IconButton(
                            onClick = { showDeleteConfirmation = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Review",
                                tint = Color.Red.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Comment
            Text(
                text = review.comment,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f),
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
              // Like and Dislike buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Like button
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onVote(VoteType.LIKE) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (userVote == VoteType.LIKE) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                            contentDescription = "Like",
                            tint = if (userVote == VoteType.LIKE) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = review.likes.toString(),
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                
                // Dislike button
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onVote(VoteType.DISLIKE) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (userVote == VoteType.DISLIKE) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                            contentDescription = "Dislike",
                            tint = if (userVote == VoteType.DISLIKE) Color(0xFFF44336) else Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = review.dislikes.toString(),
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
    
    // Delete Confirmation Dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = {
                Text(
                    text = "Delete Comment",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete your comment?",
                    color = Color.White.copy(alpha = 0.9f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Yes", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteConfirmation = false },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                ) {
                    Text("No")
                }
            },
            containerColor = Color(0xFF1A1A2E),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }
}

@Composable
fun AddReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (Float, String) -> Unit
) {
    var rating by remember { mutableFloatStateOf(5f) }
    var comment by remember { mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A2E)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Add Your Review",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Rating section
                Text(
                    text = "Rating",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    repeat(5) { index ->
                        IconButton(
                            onClick = { rating = (index + 1).toFloat() }
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < rating.toInt()) Color.Yellow else Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Text(
                        text = "${rating.toInt()}/5",
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                
                // Comment section
                Text(
                    text = "Comment",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = { 
                        Text(
                            "Share your thoughts about this movie...",
                            color = Color.White.copy(alpha = 0.6f)
                        ) 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White.copy(alpha = 0.8f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 4
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = { 
                            if (comment.isNotBlank()) {
                                onSubmit(rating, comment.trim())
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = comment.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}
