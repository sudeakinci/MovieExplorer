package com.example.moviemobileproject.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository {
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    suspend fun signUp(email: String, password: String, name: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User creation failed")
            
            // Create user document in Firestore
            val userMap = hashMapOf(
                "email" to email,
                "name" to name,
                "saved_movies" to emptyList<Map<String, Any>>()
            )
            
            firestore.collection("users")
                .document(userId)
                .set(userMap)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
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
