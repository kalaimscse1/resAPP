package com.warriortech.resb.screens

import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.R
import com.warriortech.resb.ui.components.*
import com.warriortech.resb.ui.theme.Dimensions
import com.warriortech.resb.ui.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

/**
 * LoginScreen is the main entry point for user authentication.
 * It provides a user interface for entering login credentials,
 * including company code, username, and password.
 * It handles login attempts, displays error messages,
 * and navigates to the next screen upon successful login.
 * This screen is optimized for mobile devices
 * and uses a responsive design approach.
 * @param onLoginSuccess Callback function to be invoked when login is successful.
 * @param viewModel The [LoginViewModel] instance to manage the login state and actions.
 * * This ViewModel is injected using Hilt for dependency injection.
 * * The screen includes a logo, welcome message, and a form for user input.
 * It also features a snackbar for displaying error messages.
 * * The login form includes fields for company code, username, and password,
 * with appropriate icons and validation.
 * * The password field includes a toggle for visibility.
 * * The login button is disabled while a login attempt is in progress,
 * and shows a loading state when clicked.
 * * The screen uses a vertical gradient background for visual appeal.
 * * The login process is handled asynchronously,
 * and the keyboard is hidden upon successful login or button press.
 * * @see LoginViewModel
 * * This screen is designed to be responsive and user-friendly,
 * making it suitable for mobile devices.
 * @since 1.0
 * @author WarriorTech
 * @version 1.0
 * @note This screen is part of the ResB application,
 * which is a restaurant management system.
 * * It is built using Jetpack Compose for modern UI development.
 */

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel() // Inject ViewModel
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope() // For Snackbar
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    /**
     * Effect to handle login success.
     */
    LaunchedEffect(uiState.value.loginSuccess) {
        if (uiState.value.loginSuccess) {
            keyboardController?.hide() // Hide keyboard on success
            onLoginSuccess()
            viewModel.onLoginHandled() // Reset the flag
        }
    }

    /**
     * Effect to show error messages in a Snackbar.
     */
    LaunchedEffect(uiState.value.loginError) {
        uiState.value.loginError?.let { error ->
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
                .padding(Dimensions.spacingL),
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
                        contentDescription = stringResource(R.string.app_logo),
                        modifier = Modifier.size(60.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Welcome text
            Text(
                text = stringResource(R.string.welcome_message),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.login_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            /**
             * Mobile-optimized login form card
             */
            MobileOptimizedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.spacingL)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {

                    Spacer(modifier = Modifier.height(32.dp))
                    MobileOptimizedTextField(
                        value = uiState.value.companyCode,
                        onValueChange = viewModel::onCompanyCodeChange,
                        label = "CompanyCode",
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "CompanyCode",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    MobileOptimizedTextField(
                        value = uiState.value.username,
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
                        value = uiState.value.password,
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
                                    imageVector = if (uiState.value.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (uiState.value.isPasswordVisible) "Hide Password" else "Show Password"
                                )
                            }
                        }
                    )


                    MobileOptimizedButton(
                        onClick = {
                            keyboardController?.hide() // Hide keyboard on button press
                            viewModel.attemptLogin()
                        },
                        enabled = !uiState.value.isLoading,
                        text = if (uiState.value.isLoading) "Logging in..." else "Login",
                        icon = if (uiState.value.isLoading) null else Icons.Default.Login
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Register button
                    OutlinedButton(
                        onClick = onRegisterClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.value.isLoading
                    ) {
                        Text(
                            text = stringResource(R.string.register),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
        Scaffold(
            scaffoldState = scaffoldState
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Dimensions.spacingL),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.resb_logo1),
                    contentDescription = "Restaurant Logo",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = Dimensions.spacingL)
                )

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
                    modifier = Modifier.padding(top = Dimensions.spacingS)
                )

                Spacer(modifier = Modifier.height(Dimensions.spacingXL))

                /**
                 * Mobile-optimized login form
                 */
                MobileOptimizedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MobileOptimizedTextField(
                        value = uiState.value.companyCode,
                        onValueChange = viewModel::onCompanyCodeChange,
                        label = "CompanyCode",
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "CompanyCode",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(Dimensions.spacingM))
                    MobileOptimizedTextField(
                        value = uiState.value.username,
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

                    Spacer(modifier = Modifier.height(Dimensions.spacingM))

                    MobileOptimizedTextField(
                        value = uiState.value.password,
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
                            androidx.compose.material.IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                                Icon(
                                    imageVector = if (uiState.value.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (uiState.value.isPasswordVisible) "Hide password" else "Show password",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(Dimensions.spacingL))

                    MobileOptimizedButton(
                        onClick = {
                            keyboardController?.hide()
                            viewModel.attemptLogin()
                        },
                        enabled = !uiState.value.isLoading,
                        text = if (uiState.value.isLoading) "Logging in..." else "Login",
                        icon = if (uiState.value.isLoading) null else Icons.Default.Login
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Register button
                    OutlinedButton(
                        onClick = onRegisterClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.value.isLoading
                    ) {
                        Text(
                            text = stringResource(R.string.register),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}