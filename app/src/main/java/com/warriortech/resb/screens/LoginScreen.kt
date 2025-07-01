package com.warriortech.resb.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
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

//@Composable
//fun LoginScreen(
//    onLoginSuccess: () -> Unit
//) {
//    val scaffoldState = rememberScaffoldState()
//    val coroutineScope = rememberCoroutineScope()
//
//    // State for form fields
//    var companyCode by remember { mutableStateOf("") }
//    var username by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var isPasswordVisible by remember { mutableStateOf(false) }
//
//    // State for loading
//    var isLoading by remember { mutableStateOf(false) }
//
//    Scaffold(
//        scaffoldState = scaffoldState
//    ) { paddingValues ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues),
//            contentAlignment = Alignment.Center
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 32.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                // Logo
//                Image(
//                    painter = painterResource(id = R.drawable.resb_logo1),
//                    contentDescription = "Restaurant Logo",
//                    modifier = Modifier
//                        .size(250.dp)
//                        .padding(bottom = 16.dp)
//                )
//                // Company Code Field
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
//
//                Spacer(modifier = Modifier.height(16.dp))
//
////                 Username Field
//                OutlinedTextField(
//                    value = username,
//                    onValueChange = { username = it },
//                    label = { Text("Username") },
//                    leadingIcon = {
//                        Icon(
//                            imageVector = Icons.Default.Person,
//                            contentDescription = "Username"
//                        )
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    singleLine = true
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Password Field
//                OutlinedTextField(
//                    value = password,
//                    onValueChange = { password = it },
//                    label = { Text("Password") },
//                    leadingIcon = {
//                        Icon(
//                            imageVector = Icons.Default.Lock,
//                            contentDescription = "Password"
//                        )
//                    },
//                    trailingIcon = {
//                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
//                            Icon(
//                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
//                                contentDescription = if (isPasswordVisible) "Hide Password" else "Show Password"
//                            )
//                        }
//                    },
//                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//                    modifier = Modifier.fillMaxWidth(),
//                    singleLine = true
//                )
//
//                Spacer(modifier = Modifier.height(32.dp))
//
//                // Login Button
//                Button(
//                    onClick = {
//                        if (validateInput(companyCode, username, password)) {
//                            isLoading = true
//                            coroutineScope.launch {
//                                try {
//                                    loginUser(
//                                        companyCode = companyCode,
//                                        username = username,
//                                        password = password,
//                                        onSuccess = {
//                                            isLoading = false
//                                            onLoginSuccess()
//                                        },
//                                        onError = { errorMessage ->
//                                            isLoading = false
//                                            coroutineScope.launch {
//                                                scaffoldState.snackbarHostState.showSnackbar(errorMessage)
//                                            }
//                                        }
//                                    )
//                                } catch (e: Exception) {
//                                    isLoading = false
//                                    scaffoldState.snackbarHostState.showSnackbar(
//                                        "Error: ${e.message ?: "Unknown error"}"
//                                    )
//                                }
//                            }
//                        } else {
//                            coroutineScope.launch {
//                                scaffoldState.snackbarHostState.showSnackbar(
//                                    "Please fill all fields"
//                                )
//                            }
//                        }
//                    },
//                    enabled = !isLoading,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(50.dp)
//                ) {
//                    if (isLoading) {
//                        CircularProgressIndicator(
//                            color = Color.White,
//                            modifier = Modifier.size(24.dp)
//                        )
//                    } else {
//                        Text("LOGIN")
//                    }
//                }
//            }
//        }
//    }
//}
//
//private fun validateInput(companyCode: String, username: String, password: String): Boolean {
//    return  username.isNotBlank() && password.isNotBlank() && companyCode.isNotBlank()
//}
//
//private suspend fun loginUser(
//    companyCode: String,
//    username: String,
//    password: String,
//    onSuccess: () -> Unit,
//    onError: (String) -> Unit
//) {
//    try {
//        val response = RetrofitClient.apiService.login(LoginRequest(companyCode = companyCode, user_name = username, password = password))
//
//        if (response.success && response.data != null) {
//            val authResponse = response.data!!
//            // Save authentication token
//            SessionManager.saveAuthToken(authResponse.token)
//            // Save user data
//            SessionManager.saveUser(authResponse.user)
//            // Save company code
//            SessionManager.saveCompanyCode(companyCode)
//            onSuccess()
//        } else {
//            onError("Login failed: ${response.message}")
//        }
//    } catch (e: Exception) {
//        onError("Error: ${e.message ?: "Unknown error"}")
//    }
//}



import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import com.warriortech.resb.ui.viewmodel.LoginViewModel // Import your ViewModel
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.ui.theme.*
import com.warriortech.resb.ui.viewmodel.LoginViewModel
import com.warriortech.resb.ui.components.MobileOptimizedTextField
import com.warriortech.resb.ui.components.MobileOptimizedButton
import com.warriortech.resb.ui.components.MobileOptimizedCard

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel() // Inject ViewModel
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope() // For Snackbar
    val uiState = viewModel.uiState
    val keyboardController = LocalSoftwareKeyboardController.current

    // Observe login success
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            keyboardController?.hide() // Hide keyboard on success
            onLoginSuccess()
            viewModel.onLoginHandled() // Reset the flag
        }
    }

    // Show Snackbar for errors
    LaunchedEffect(uiState.loginError) {
        uiState.loginError?.let { error ->
            coroutineScope.launch {
                scaffoldState.snackbarHostState.showSnackbar(error)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo section
            Card(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.resb_logo_1),
                        contentDescription = "RESB Logo",
                        modifier = Modifier.size(60.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Welcome text
            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Sign in to continue",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Login form card
            MobileOptimizedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.spacingL)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    MobileOptimizedTextField(
                        value = uiState.username,
                        onValueChange = viewModel::onUsernameChange,
                        label = "Username",
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Username",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MobileOptimizedTextField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChange,
                        label = "Password",
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                                Icon(
                                    imageVector = if (uiState.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (uiState.isPasswordVisible) "Hide Password" else "Show Password"
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    MobileOptimizedButton(
                        onClick = {
                            keyboardController?.hide() // Hide keyboard on button press
                            viewModel.attemptLogin()
                        },
                        enabled = !uiState.isLoading,
                        text = if (uiState.isLoading) "Logging in..." else "Login",
                        icon = if (uiState.isLoading) null else Icons.Default.Login
                    )
                }
            }
        }
    }

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
                Image(
                    painter = painterResource(id = R.drawable.resb_logo1),
                    contentDescription = "Restaurant Logo",
                    modifier = Modifier
                        .size(250.dp)
                        .padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = uiState.companyCode,
                    onValueChange = { viewModel.onCompanyCodeChange(it) },
                    label = { Text("Company Code") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = "Company Code"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.loginError != null // Optionally highlight field on error
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.username,
                    onValueChange = { viewModel.onUsernameChange(it) },
                    label = { Text("Username") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Username"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.loginError != null
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password"
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                            Icon(
                                imageVector = if (uiState.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (uiState.isPasswordVisible) "Hide Password" else "Show Password"
                            )
                        }
                    },
                    visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.loginError != null
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        keyboardController?.hide() // Hide keyboard on button press
                        viewModel.attemptLogin()
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White, // Material Design guidelines suggest this should be MaterialTheme.colors.onPrimary
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