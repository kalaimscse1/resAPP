package com.warriortech.resb.ui.viewmodel

import android.util.Log
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

/**
 * ViewModel for managing the login state and actions.
 */
data class LoginUiState(
    val companyCode: String = "",
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val loginError: String? = null,
    val loginSuccess: Boolean = false
)
/**
 * ViewModel for handling user login functionality.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() { // Assuming you might inject dependencies later

    var uiState by mutableStateOf(LoginUiState())
        private set
    /**
     * Handles changes to the company code input field.
     */
    fun onCompanyCodeChange(companyCode: String) {
        uiState = uiState.copy(companyCode = companyCode, loginError = null)
    }

    /**
     * Handles changes to the username input field.
     */
    fun onUsernameChange(username: String) {
        uiState = uiState.copy(username = username, loginError = null)
    }

    /**
     * Handles changes to the password input field.
     */
    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, loginError = null)
    }

    /**
     * Toggles the visibility of the password input field.
     */
    fun togglePasswordVisibility() {
        uiState = uiState.copy(isPasswordVisible = !uiState.isPasswordVisible)
    }

    /**
     * Validates the input fields to ensure they are not empty.
     */
    private fun validateInput(): Boolean {
        return uiState.username.isNotBlank() &&
                uiState.password.isNotBlank() &&
                uiState.companyCode.isNotBlank()
    }
    /**
     * Login function that attempts to log in the user with the provided credentials.
     */

    fun attemptLogin() {
        if (!validateInput()) {
            uiState = uiState.copy(loginError = "Please fill all fields")
            return
        }

        uiState = uiState.copy(isLoading = true, loginError = null)
        viewModelScope.launch {
            try {
                val check = RetrofitClient.apiService.checkIsBlock(uiState.companyCode)
                Log.d("checkIsBlock", check.toString())
                if (check.data!!){
                    val response = RetrofitClient.apiService.login(
                        request=LoginRequest(
                            companyCode = uiState.companyCode,
                            user_name = uiState.username,
                            password = uiState.password
                        ),
                        tenantId = uiState.companyCode
                    )

                    if (response.success && response.data != null) {
                        val authResponse = response.data
                        sessionManager.saveAuthToken(authResponse.token)
                        sessionManager.saveUser(authResponse.user)
                        sessionManager.saveCompanyCode(uiState.companyCode)
                        uiState = uiState.copy(isLoading = false, loginSuccess = true)
                    } else {
                        uiState = uiState.copy(isLoading = false, loginError = "Login failed: ${response.message}")
                    }

                }
                else{
                    uiState = uiState.copy(isLoading = false, loginError = check.message)
                }

            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, loginError = "Error: ${e.message ?: "Unknown error"}")
            }
        }
    }

    /**
     * Resets the login success state after handling the login result.
     */
    fun onLoginHandled() {
        uiState = uiState.copy(loginSuccess = false, loginError = null)
    }
}