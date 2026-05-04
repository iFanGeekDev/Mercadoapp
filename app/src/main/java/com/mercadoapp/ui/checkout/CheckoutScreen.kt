package com.mercadoapp.ui.checkout

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mercadoapp.domain.model.CartItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutRoute(
    onBack: () -> Unit,
    onDone: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.step) {
        if (state.step is CheckoutStep.Confirmed) {
            kotlinx.coroutines.delay(2_500)
            onDone()
        }
    }

    CheckoutScreen(
        state = state,
        onBack = {
            if (state.step is CheckoutStep.Summary) onBack()
            else viewModel.goBack()
        },
        onFullNameChanged  = viewModel::onFullNameChanged,
        onAddressChanged   = viewModel::onAddressChanged,
        onCityChanged      = viewModel::onCityChanged,
        onPhoneChanged     = viewModel::onPhoneChanged,
        onCardNumberChanged = viewModel::onCardNumberChanged,
        onCardExpiryChanged = viewModel::onCardExpiryChanged,
        onCardCvcChanged   = viewModel::onCardCvcChanged,
        onProceedToPayment = viewModel::proceedToPayment,
        onConfirmOrder     = viewModel::confirmOrder
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CheckoutScreen(
    state: CheckoutUiState,
    onBack: () -> Unit,
    onFullNameChanged: (String) -> Unit,
    onAddressChanged: (String) -> Unit,
    onCityChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onCardNumberChanged: (String) -> Unit,
    onCardExpiryChanged: (String) -> Unit,
    onCardCvcChanged: (String) -> Unit,
    onProceedToPayment: () -> Unit,
    onConfirmOrder: () -> Unit
) {
    if (state.step is CheckoutStep.Confirmed) {
        ConfirmedScreen()
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (state.step) {
                            is CheckoutStep.Summary -> "Resumen del pedido"
                            is CheckoutStep.Payment -> "Datos de pago"
                            else -> ""
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 4.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (state.error != null) {
                        Text(
                            state.error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total", style = MaterialTheme.typography.bodySmall)
                            Text(
                                "USD ${"%.2f".format(state.total)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Button(
                            onClick = when (state.step) {
                                is CheckoutStep.Summary -> onProceedToPayment
                                is CheckoutStep.Payment -> onConfirmOrder
                                else -> ({})
                            },
                            enabled = !state.isLoading,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary)
                            } else {
                                Text(
                                    when (state.step) {
                                        is CheckoutStep.Summary -> "Ir al pago"
                                        is CheckoutStep.Payment -> "Confirmar pedido"
                                        else -> ""
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        AnimatedContent(
            targetState = state.step,
            transitionSpec = {
                slideInHorizontally { it } + fadeIn() togetherWith
                slideOutHorizontally { -it } + fadeOut()
            },
            label = "step_transition"
        ) { step ->
            when (step) {
                is CheckoutStep.Summary -> SummaryStep(
                    items = state.items,
                    padding = padding
                )
                is CheckoutStep.Payment -> PaymentStep(
                    state = state,
                    padding = padding,
                    onFullNameChanged = onFullNameChanged,
                    onAddressChanged = onAddressChanged,
                    onCityChanged = onCityChanged,
                    onPhoneChanged = onPhoneChanged,
                    onCardNumberChanged = onCardNumberChanged,
                    onCardExpiryChanged = onCardExpiryChanged,
                    onCardCvcChanged = onCardCvcChanged
                )
                else -> {}
            }
        }
    }
}

@Composable
private fun SummaryStep(items: List<CartItem>, padding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Productos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        items.forEach { item ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.productName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text(
                            "${item.variant.condition.name} · ${item.variant.color} · ${item.variant.ramGb}GB",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        "x${item.quantity}  USD ${"%.2f".format(item.subtotal)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentStep(
    state: CheckoutUiState,
    padding: PaddingValues,
    onFullNameChanged: (String) -> Unit,
    onAddressChanged: (String) -> Unit,
    onCityChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onCardNumberChanged: (String) -> Unit,
    onCardExpiryChanged: (String) -> Unit,
    onCardCvcChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Envío", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        OutlinedTextField(state.fullName, onFullNameChanged, label = { Text("Nombre completo") },
            leadingIcon = { Icon(Icons.Default.Person, null) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(state.address, onAddressChanged, label = { Text("Dirección") },
            leadingIcon = { Icon(Icons.Default.Home, null) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(state.city, onCityChanged, label = { Text("Ciudad") },
            leadingIcon = { Icon(Icons.Default.LocationCity, null) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(state.phone, onPhoneChanged, label = { Text("Teléfono") },
            leadingIcon = { Icon(Icons.Default.Phone, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(), singleLine = true)

        Spacer(Modifier.height(4.dp))
        HorizontalDivider()
        Spacer(Modifier.height(4.dp))

        Text("Tarjeta de crédito/débito", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        OutlinedTextField(state.cardNumber, onCardNumberChanged, label = { Text("Número de tarjeta") },
            leadingIcon = { Icon(Icons.Default.CreditCard, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            placeholder = { Text("1234 5678 9012 3456") })
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(state.cardExpiry, onCardExpiryChanged, label = { Text("MM/AA") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f), singleLine = true)
            OutlinedTextField(state.cardCvc, onCardCvcChanged, label = { Text("CVC") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f), singleLine = true)
        }
    }
}

@Composable
private fun ConfirmedScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp)
            )
            Text("¡Pedido confirmado!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Te enviaremos los detalles por email.", style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
