package com.mercadoapp.ui.checkout

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mercadoapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutRoute(
    onBack: () -> Unit,
    onPaymentSuccess: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state.isSuccess) { if (state.isSuccess) onPaymentSuccess() }
    CheckoutScreen(state = state, onBack = onBack, onConfirm = viewModel::confirmOrder)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CheckoutScreen(
    state: CheckoutUiState,
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    Scaffold(
        containerColor = Dark900,
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.background(Dark800, CircleShape)) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
                Text("Payment", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Box(modifier = Modifier.size(48.dp))
            }
        },
        bottomBar = {
            Surface(color = Dark800, shadowElevation = 16.dp, shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.fillMaxWidth().height(56.dp).background(Brush.horizontalGradient(listOf(Brand600, Brand400)), RoundedCornerShape(16.dp))) {
                        Button(
                            onClick = onConfirm,
                            enabled = !state.isLoading,
                            modifier = Modifier.fillMaxSize(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            if (state.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            else Text("PAY NOW", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    ) { padding ->

        if (state.isInitialLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Brand500) }
            return@Scaffold
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Steps indicator
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                StepIndicator(step = 1, title = "Cart", isCompleted = true)
                HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), color = Brand500)
                StepIndicator(step = 2, title = "Payment", isCompleted = false, isActive = true)
                HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), color = Dark700)
                StepIndicator(step = 3, title = "Confirm", isCompleted = false)
            }

            Text("Payment Method", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)

            // Visa Card 4242
            Card(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF1E1E2E), Color(0xFF2E2E45))))) {
                    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.SpaceBetween) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Credit Card", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                            Text("VISA", color = Color.White, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                        }
                        Text("**** **** **** 4242", color = Color.White, style = MaterialTheme.typography.titleLarge, letterSpacing = 2.sp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Card Holder", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                                Text("ALEX EXPLORER", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                            }
                            Column {
                                Text("Expires", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                                Text("12/28", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            // Other payment options
            PaymentOption(icon = Icons.Default.CreditCard, title = "Add new card")
            PaymentOption(icon = Icons.Default.AccountBalanceWallet, title = "Apple Pay")
            PaymentOption(icon = Icons.Default.AccountBalanceWallet, title = "Google Pay")
        }
    }
}

@Composable
private fun StepIndicator(step: Int, title: String, isCompleted: Boolean, isActive: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier.size(32.dp).background(if (isCompleted || isActive) Brand500 else Dark800, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
            else Text(step.toString(), color = if (isActive) Color.White else TextSecondary, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        }
        Text(title, style = MaterialTheme.typography.labelSmall, color = if (isActive || isCompleted) Color.White else TextSecondary)
    }
}

@Composable
private fun PaymentOption(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(
        modifier = Modifier.fillMaxWidth().border(1.dp, Dark700, RoundedCornerShape(16.dp)).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(icon, null, tint = TextSecondary)
        Text(title, style = MaterialTheme.typography.bodyMedium, color = Color.White, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, null, tint = TextSecondary)
    }
}
