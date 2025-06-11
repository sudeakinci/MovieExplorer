package com.example.moviemobileproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviemobileproject.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    
    private val authRepository = AuthRepository()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    private val _isAuthenticated = MutableStateFlow(authRepository.isUserLoggedIn())
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated
    
    fun signUp(email: String, password: String, name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            authRepository.signUp(email, password, name)
                .onSuccess {
                    _isAuthenticated.value = true
                    onSuccess()
                }
                .onFailure {
                    _errorMessage.value = it.message
                }
            
            _isLoading.value = false
        }
    }
    
    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            authRepository.signIn(email, password)
                .onSuccess {
                    _isAuthenticated.value = true
                    onSuccess()
                }
                .onFailure {
                    _errorMessage.value = it.message
                }
            
            _isLoading.value = false
        }
    }
    
    fun signOut() {
        authRepository.signOut()
        _isAuthenticated.value = false
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}
