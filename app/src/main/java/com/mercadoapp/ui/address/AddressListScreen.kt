package com.mercadoapp.ui.address

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mercadoapp.domain.model.Address
import com.mercadoapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressListRoute(
    onBack: () -> Unit,
    onAddAddress: () -> Unit,
    onEditAddress: (String) -> Unit,
    viewModel: AddressListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    AddressListScreen(
        state = state,
        onBack = onBack,
        onAddAddress = onAddAddress,
        onEditAddress = onEditAddress,
        onSetDefault = viewModel::setDefault,
        onDelete = viewModel::deleteAddress
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressListScreen(
    state: AddressListUiState,
    onBack: () -> Unit,
    onAddAddress: () -> Unit,
    onEditAddress: (String) -> Unit,
    onSetDefault: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    Scaffold(
        modifier = Modifier.systemBarsPadding(),
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
                Text("Mis Direcciones", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                IconButton(onClick = onAddAddress, modifier = Modifier.background(Brand500.copy(alpha = 0.15f), CircleShape)) {
                    Icon(Icons.Default.Add, null, tint = Brand400)
                }
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Brand500) }
            return@Scaffold
        }

        if (state.addresses.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(64.dp), tint = Dark700)
                    Text("No hay direcciones guardadas", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
                    Button(onClick = onAddAddress, colors = ButtonDefaults.buttonColors(containerColor = Brand500)) {
                        Text("Añadir Dirección")
                    }
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.addresses, key = { it.id }) { address ->
                AddressCard(
                    address = address,
                    onEdit = { onEditAddress(address.id) },
                    onSetDefault = { onSetDefault(address.id) },
                    onDelete = { onDelete(address.id) }
                )
            }
        }
    }
}

@Composable
private fun AddressCard(
    address: Address,
    onEdit: () -> Unit,
    onSetDefault: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (address.isDefault) Dark800 else Dark900),
        border = if (address.isDefault) null else androidx.compose.foundation.BorderStroke(1.dp, Dark700)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(if (address.alias.equals("casa", true) || address.alias.equals("home", true)) Icons.Default.Home else Icons.Default.LocationOn, null, tint = Brand400, modifier = Modifier.size(20.dp))
                    Text(address.alias, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    if (address.isDefault) {
                        Surface(color = Brand500.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
                            Text("PRINCIPAL", style = MaterialTheme.typography.labelSmall, color = Brand400, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Edit, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                }
            }

            Text("${address.street}\n${address.distrito}, ${address.provincia}, ${address.departamento}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, lineHeight = 20.sp)

            if (!address.isDefault) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onDelete) {
                        Text("Eliminar", color = ErrorRed)
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = onSetDefault) {
                        Text("Hacer principal", color = Brand400)
                    }
                }
            }
        }
    }
}
