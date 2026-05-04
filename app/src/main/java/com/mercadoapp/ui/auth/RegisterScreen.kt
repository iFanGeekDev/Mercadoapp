package com.mercadoapp.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state.isSuccess) { if (state.isSuccess) onRegisterSuccess() }
    RegisterScreen(state = state, onNameChanged = viewModel::onNameChanged,
        onEmailChanged = viewModel::onEmailChanged, onPasswordChanged = viewModel::onPasswordChanged,
        onConfirmPasswordChanged = viewModel::onConfirmPasswordChanged,
        onRegister = viewModel::register, onNavigateToLogin = onNavigateToLogin)
}

@Composable
private fun RegisterScreen(
    state: RegisterUiState,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onRegister: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible  by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(
                colors = listOf(Accent600.copy(alpha = 0.5f), Dark900),
                center = Offset(0.8f, 0.15f),
                radius = 1000f
            )))

        // Decorative orbs
        Box(modifier = Modifier.size(220.dp).offset(x = 200.dp, y = (-30).dp)
            .background(Accent500.copy(alpha = 0.12f), CircleShape))
        Box(modifier = Modifier.size(160.dp).offset(x = (-40).dp, y = 600.dp)
            .background(Brand500.copy(alpha = 0.10f), CircleShape))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(64.dp))

            // Logo
            Box(
                modifier = Modifier.size(64.dp).background(
                    Brush.linearGradient(listOf(Accent500, Brand500)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(30.dp), tint = Color.White)
            }

            Spacer(Modifier.height(20.dp))
            Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold, color = Color.White)
            Text("Completá tus datos para registrarte",
                style = MaterialTheme.typography.bodyMedium, color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp))

            Spacer(Modifier.height(32.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Dark700.copy(alpha = 0.90f)),
                elevation = CardDefaults.cardElevation(0.dp)) {
                Column(modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)) {

                    OutlinedTextField(state.name, onNameChanged,
                        label = { Text("Nombre completo") },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = Accent400) },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp), isError = state.error != null,
                        colors = authFieldColors())

                    OutlinedTextField(state.email, onEmailChanged,
                        label = { Text("Correo electrónico") },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = Accent400) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp), isError = state.error != null,
                        colors = authFieldColors())

                    OutlinedTextField(
                        value = state.password, onValueChange = onPasswordChanged,
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Accent400) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    null, tint = TextSecondary)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp), isError = state.error != null,
                        colors = authFieldColors())

                    OutlinedTextField(
                        value = state.confirmPassword, onValueChange = onConfirmPasswordChanged,
                        label = { Text("Confirmar contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Accent400) },
                        trailingIcon = {
                            IconButton(onClick = { confirmVisible = !confirmVisible }) {
                                Icon(if (confirmVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    null, tint = TextSecondary)
                            }
                        },
                        visualTransformation = if (confirmVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp), isError = state.error != null,
                        colors = authFieldColors())

                    if (state.error != null) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Error, null, Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.error)
                            Text(state.error, color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Button(onClick = onRegister, enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Accent500)) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp),
                                color = Color.White, strokeWidth = 2.5.dp)
                        } else {
                            Text("Registrarme", fontWeight = FontWeight.SemiBold, color = Dark900)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            TextButton(onClick = onNavigateToLogin) {
                Text("¿Ya tenés cuenta? ", color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium)
                Text("Iniciá sesión", color = Accent400,
                    style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}
