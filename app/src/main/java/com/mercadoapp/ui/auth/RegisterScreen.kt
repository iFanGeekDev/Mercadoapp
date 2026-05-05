package com.mercadoapp.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mercadoapp.ui.theme.*

@Composable
fun RegisterRoute(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state.isSuccess) { if (state.isSuccess) onRegisterSuccess() }
    
    RegisterScreen(
        state = state,
        onNameChanged = viewModel::onNameChanged,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onConfirmPasswordChanged = viewModel::onConfirmPasswordChanged,
        onRegister = viewModel::register,
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
private fun RegisterScreen(
    state: RegisterUiState,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onRegister: () -> Unit,
    onNavigateToLogin: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Dark900).systemBarsPadding()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.radialGradient(colors = listOf(Brand700.copy(alpha = 0.5f), Dark900), center = Offset(0.5f, 0.2f), radius = 1200f))
        )
        Box(modifier = Modifier.size(280.dp).offset(x = (-60).dp, y = (-40).dp).background(Brand500.copy(alpha = 0.15f), CircleShape).blur(80.dp))
        Box(modifier = Modifier.size(200.dp).offset(x = 200.dp, y = 580.dp).background(Accent500.copy(alpha = 0.10f), CircleShape).blur(60.dp))

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(60.dp))

            Box(
                modifier = Modifier.size(72.dp).background(Brush.linearGradient(listOf(Brand500, Accent500)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("M", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp)
            }

            Spacer(Modifier.height(32.dp))

            Text("Initialize your ecosystem access.", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
            Text("Create an account to continue", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, modifier = Modifier.padding(top = 8.dp))

            Spacer(Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Dark800.copy(alpha = 0.8f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = state.name, onValueChange = onNameChanged, label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = Brand400) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = authFieldColors()
                    )

                    OutlinedTextField(
                        value = state.email, onValueChange = onEmailChanged, label = { Text("Email address") },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = Brand400) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        singleLine = true, isError = state.error != null, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = authFieldColors()
                    )

                    OutlinedTextField(
                        value = state.password, onValueChange = onPasswordChanged, label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Brand400) },
                        trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = TextSecondary) } },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = authFieldColors()
                    )

                    OutlinedTextField(
                        value = state.confirmPassword, onValueChange = onConfirmPasswordChanged, label = { Text("Confirm Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Brand400) },
                        trailingIcon = { IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) { Icon(if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = TextSecondary) } },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onRegister() }),
                        singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = authFieldColors()
                    )

                    if (state.error != null) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Error, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.error)
                            Text(state.error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth().height(52.dp).background(Brush.horizontalGradient(listOf(Brand600, Brand400)), RoundedCornerShape(14.dp))
                    ) {
                        Button(
                            onClick = onRegister, enabled = !state.isLoading, modifier = Modifier.fillMaxSize(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.5.dp)
                            else Text("CREATE ACCOUNT", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            TextButton(onClick = onNavigateToLogin) {
                Text("Already have an account? ", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                Text("Sign in", color = Brand400, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}
