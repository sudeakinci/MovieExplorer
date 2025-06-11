package com.example.moviemobileproject.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
      suspend fun signUp(email: String, password: String, name: String): Result<Unit> {
        return try {
            // Validate input
            if (email.isBlank()) throw Exception("Email cannot be empty")
            if (password.length < 6) throw Exception("Password must be at least 6 characters")
            if (name.isBlank()) throw Exception("Name cannot be empty")
            
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User creation failed")
            
            // Create user document in Firestore
            val userMap = hashMapOf(
                "email" to email,
                "name" to name,
                "saved_movies" to emptyList<Map<String, Any>>(),
                "created_at" to System.currentTimeMillis()
            )
            
            firestore.collection("users")
                .document(userId)
                .set(userMap)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("network error") == true -> "Network error. Please check your internet connection."
                e.message?.contains("email-already-in-use") == true -> "This email is already registered."
                e.message?.contains("invalid-email") == true -> "Please enter a valid email address."
                e.message?.contains("weak-password") == true -> "Password is too weak. Please use at least 6 characters."
                e.message?.contains("internal-error") == true -> "Firebase configuration error. Please check your setup."
                else -> e.message ?: "An unknown error occurred during sign up."
            }
            Result.failure(Exception(errorMessage))
        }
    }
      suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            // Validate input
            if (email.isBlank()) throw Exception("Email cannot be empty")
            if (password.isBlank()) throw Exception("Password cannot be empty")
            
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("user-not-found") == true -> "No account found with this email address."
                e.message?.contains("wrong-password") == true -> "Incorrect password. Please try again."
                e.message?.contains("invalid-email") == true -> "Please enter a valid email address."
                e.message?.contains("user-disabled") == true -> "This account has been disabled."
                e.message?.contains("too-many-requests") == true -> "Too many failed attempts. Please try again later."
                e.message?.contains("network error") == true -> "Network error. Please check your internet connection."
                e.message?.contains("internal-error") == true -> "Firebase configuration error. Please check your setup."
                else -> e.message ?: "An unknown error occurred during sign in."
            }
            Result.failure(Exception(errorMessage))
        }
    }
    
    fun signOut() {
        auth.signOut()
    }
    
    fun getCurrentUser() = auth.currentUser
    
    fun isUserLoggedIn() = auth.currentUser != null
    
    suspend fun getUserData(): Result<Map<String, Any>?> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            val snapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            
            Result.success(snapshot.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
