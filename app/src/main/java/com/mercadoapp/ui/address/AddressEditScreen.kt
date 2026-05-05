package com.mercadoapp.ui.address

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mercadoapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressEditRoute(
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AddressEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state.isSuccess) { if (state.isSuccess) onSaveSuccess() }
    AddressEditScreen(
        state = state,
        onBack = onBack,
        onAliasChanged = viewModel::onAliasChanged,
        onStreetChanged = viewModel::onStreetChanged,
        onDistritoSelected = viewModel::selectDistrict,
        onProvinciaSelected = viewModel::selectProvince,
        onDepartamentoSelected = viewModel::selectDepartment,
        onIsDefaultChanged = viewModel::onIsDefaultChanged,
        onSave = viewModel::saveAddress
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressEditScreen(
    state: AddressEditUiState,
    onBack: () -> Unit,
    onAliasChanged: (String) -> Unit,
    onStreetChanged: (String) -> Unit,
    onDistritoSelected: (String, String) -> Unit,
    onProvinciaSelected: (String, String) -> Unit,
    onDepartamentoSelected: (String, String) -> Unit,
    onIsDefaultChanged: (Boolean) -> Unit,
    onSave: () -> Unit
) {
    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        containerColor = Dark900,
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.background(Dark800, CircleShape)) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
                Spacer(Modifier.width(16.dp))
                Text(if (state.id.isEmpty()) "Añadir Dirección" else "Editar Dirección", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        bottomBar = {
            Surface(color = Dark800, shadowElevation = 16.dp, shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp).height(56.dp).background(Brush.horizontalGradient(listOf(Brand600, Brand400)), RoundedCornerShape(16.dp))) {
                    Button(
                        onClick = onSave,
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxSize(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        if (state.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        else Text("GUARDAR DIRECCIÓN", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.alias, onValueChange = onAliasChanged, label = { Text("Nombre de dirección (Ej. Casa)") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = addressFieldColors(), singleLine = true
            )
            OutlinedTextField(
                value = state.street, onValueChange = onStreetChanged, label = { Text("Dirección (Calle, Urb, Nro)") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = addressFieldColors(), singleLine = true
            )
            UbigeoDropdown(
                label = "Departamento",
                options = state.departments,
                selectedName = state.departamento,
                onOptionSelected = onDepartamentoSelected,
                modifier = Modifier.fillMaxWidth()
            )
            UbigeoDropdown(
                label = "Provincia",
                options = state.provinces,
                selectedName = state.provincia,
                onOptionSelected = onProvinciaSelected,
                modifier = Modifier.fillMaxWidth(),
                enabled = state.provinces.isNotEmpty()
            )
            UbigeoDropdown(
                label = "Distrito",
                options = state.districts,
                selectedName = state.distrito,
                onOptionSelected = onDistritoSelected,
                modifier = Modifier.fillMaxWidth(),
                enabled = state.districts.isNotEmpty()
            )
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Checkbox(checked = state.isDefault, onCheckedChange = onIsDefaultChanged, colors = CheckboxDefaults.colors(checkedColor = Brand500, uncheckedColor = TextSecondary))
                Text("Usar como dirección de envío principal", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun addressFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedBorderColor  = Dark400,
    focusedBorderColor    = Brand500,
    unfocusedLabelColor   = TextSecondary,
    focusedLabelColor     = Brand400,
    cursorColor           = Brand500,
    unfocusedContainerColor = Dark800,
    focusedContainerColor   = Dark800,
    unfocusedTextColor    = TextPrimary,
    focusedTextColor      = TextPrimary
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UbigeoDropdown(
    label: String,
    options: List<com.mercadoapp.data.remote.dto.UbigeoDto>,
    selectedName: String,
    onOptionSelected: (id: String, name: String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = addressFieldColors(),
            singleLine = true,
            enabled = enabled
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Dark800)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name, color = TextPrimary) },
                    onClick = {
                        onOptionSelected(option.id, option.name)
                        expanded = false
                    }
                )
            }
        }
    }
}
