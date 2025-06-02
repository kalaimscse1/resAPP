package com.warriortech.resb.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.warriortech.resb.R
import com.warriortech.resb.model.LoginRequest
import com.warriortech.resb.network.RetrofitClient
import com.warriortech.resb.network.SessionManager
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    // State for form fields
    var companyCode by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // State for loading
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        scaffoldState = scaffoldState
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.resb_logo1),
                    contentDescription = "Restaurant Logo",
                    modifier = Modifier
                        .size(250.dp)
                        .padding(bottom = 16.dp)
                )
                // Company Code Field
//                OutlinedTextField(
//                    value = companyCode,
//                    onValueChange = { companyCode = it },
//                    label = { Text("Company Code") },
//                    leadingIcon = {
//                        Icon(
//                            imageVector = Icons.Default.Business,
//                            contentDescription = "Company Code"
//                        )
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    singleLine = true
//                )

                Spacer(modifier = Modifier.height(16.dp))

//                 Username Field
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Username"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password"
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isPasswordVisible) "Hide Password" else "Show Password"
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Login Button
                Button(
                    onClick = {
                        if (validateInput(companyCode, username, password)) {
                            isLoading = true
                            coroutineScope.launch {
                                try {
                                    loginUser(
                                        username = username,
                                        password = password,
                                        onSuccess = {
                                            isLoading = false
                                            onLoginSuccess()
                                        },
                                        onError = { errorMessage ->
                                            isLoading = false
                                            coroutineScope.launch {
                                                scaffoldState.snackbarHostState.showSnackbar(errorMessage)
                                            }
                                        }
                                    )
                                } catch (e: Exception) {
                                    isLoading = false
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        "Error: ${e.message ?: "Unknown error"}"
                                    )
                                }
                            }
                        } else {
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    "Please fill all fields"
                                )
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("LOGIN")
                    }
                }
            }
        }
    }
}

private fun validateInput(companyCode: String, username: String, password: String): Boolean {
    return  username.isNotBlank() && password.isNotBlank()
}

private suspend fun loginUser(
//    companyCode: String,
    username: String,
    password: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    try {
        val response = RetrofitClient.apiService.login(LoginRequest(user_name = username, password = password))

        if (response.success && response.data != null) {
            val authResponse = response.data!!
            // Save authentication token
            SessionManager.saveAuthToken(authResponse.token)
            // Save user data
            SessionManager.saveUser(authResponse.user)
            // Save company code
//            SessionManager.saveCompanyCode(companyCode)
            onSuccess()
        } else {
            onError("Login failed: ${response.message}")
        }
    } catch (e: Exception) {
        onError("Error: ${e.message ?: "Unknown error"}")
    }
}