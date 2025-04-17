package com.akbaria.genc.peresantation.viewmodel

import com.akbaria.genc.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akbaria.genc.data.repository.ArtRepository
import com.akbaria.genc.domain.util.Resource
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: ArtRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState())
    val authState = _authState.asStateFlow()

    private val _userState = MutableStateFlow<UserState>(UserState())
    val userState = _userState.asStateFlow()

    init {
        getCurrentUser()
    }

    fun registerUser(email: String, password: String, name: String, isSeller: Boolean) {
        viewModelScope.launch {
            _authState.value = AuthState(isLoading = true)
            repository.registerUser(email, password, name, isSeller).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _authState.value = AuthState(isAuthenticated = true)
                        getCurrentUser()
                    }
                    is Resource.Error -> {
                        _authState.value = AuthState(error = result.message ?: "Bilinməyən xəta")
                    }
                    is Resource.Loading -> {
                        _authState.value = AuthState(isLoading = true)
                    }
                }
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState(isLoading = true)
            repository.loginUser(email, password).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _authState.value = AuthState(isAuthenticated = true)
                        getCurrentUser()
                    }
                    is Resource.Error -> {
                        _authState.value = AuthState(error = result.message ?: "Bilinməyən xəta")
                    }
                    is Resource.Loading -> {
                        _authState.value = AuthState(isLoading = true)
                    }
                }
            }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            repository.getCurrentUser().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _userState.value = UserState(user = result.data)
                        _authState.value = AuthState(isAuthenticated = true)
                    }
                    is Resource.Error -> {
                        _userState.value = UserState(error = result.message ?: "İstifadəçi tapılmadı")
                        _authState.value = AuthState(isAuthenticated = false)
                    }
                    is Resource.Loading -> {
                        _userState.value = UserState(isLoading = true)
                    }
                }
            }
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState()
    }

    // State sinifləri
    data class AuthState(
        val isLoading: Boolean = false,
        val isAuthenticated: Boolean = false,
        val error: String = ""
    )

    data class UserState(
        val isLoading: Boolean = false,
        val user: User? = null,
        val error: String = ""
    )
}
