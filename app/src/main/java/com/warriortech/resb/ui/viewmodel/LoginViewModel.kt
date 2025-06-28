package com.warriortech.resb.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.model.LoginRequest
import com.warriortech.resb.network.RetrofitClient
import com.warriortech.resb.network.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// Data class to represent the UI state of the Login screen
data class LoginUiState(
    val companyCode: String = "",
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val loginError: String? = null,
    val loginSuccess: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() { // Assuming you might inject dependencies later

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onCompanyCodeChange(companyCode: String) {
        uiState = uiState.copy(companyCode = companyCode, loginError = null)
    }

    fun onUsernameChange(username: String) {
        uiState = uiState.copy(username = username, loginError = null)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, loginError = null)
    }

    fun togglePasswordVisibility() {
        uiState = uiState.copy(isPasswordVisible = !uiState.isPasswordVisible)
    }

    private fun validateInput(): Boolean {
        return uiState.username.isNotBlank() &&
                uiState.password.isNotBlank() &&
                uiState.companyCode.isNotBlank()
    }

    fun attemptLogin() {
        if (!validateInput()) {
            uiState = uiState.copy(loginError = "Please fill all fields")
            return
        }

        uiState = uiState.copy(isLoading = true, loginError = null)
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.login(
                    LoginRequest(
                        companyCode = uiState.companyCode,
                        user_name = uiState.username,
                        password = uiState.password
                    )
                )

                if (response.success && response.data != null) {
                    val authResponse = response.data
                    SessionManager.saveAuthToken(authResponse.token)
                    SessionManager.saveUser(authResponse.user)
                    SessionManager.saveCompanyCode(uiState.companyCode)
                    uiState = uiState.copy(isLoading = false, loginSuccess = true)
                } else {
                    uiState = uiState.copy(isLoading = false, loginError = "Login failed: ${response.message}")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, loginError = "Error: ${e.message ?: "Unknown error"}")
            }
        }
    }

    // Call this after navigation to reset the success flag
    fun onLoginHandled() {
        uiState = uiState.copy(loginSuccess = false, loginError = null)
    }
}