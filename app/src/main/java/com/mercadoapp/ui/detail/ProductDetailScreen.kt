package com.mercadoapp.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mercadoapp.ui.components.SelectableChips
import coil.compose.AsyncImage

@Composable
fun ProductDetailRoute(
    onBack: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    ProductDetailScreen(state = state, onBack = onBack, viewModel = viewModel)
}

@Composable
private fun ProductDetailScreen(
    state: ProductDetailUiState,
    onBack: () -> Unit,
    viewModel: ProductDetailViewModel
) {
    Scaffold { padding ->
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(padding))
            return@Scaffold
        }

        val product = state.product ?: run {
            Text("Producto no encontrado", modifier = Modifier.padding(padding))
            return@Scaffold
        }

        val options = state.optionsState ?: EmptyOptions

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(product.name, style = MaterialTheme.typography.headlineSmall)
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            )
            Text("Descripción: ${product.shortDescription}")

            SelectableChips(
                title = "Condición",
                options = options.conditions,
                selected = state.selection.condition,
                toLabel = { it.name.lowercase().replaceFirstChar(Char::uppercaseChar) },
                onSelected = viewModel::onConditionChanged
            )

            SelectableChips(
                title = "Procesador",
                options = options.processors,
                selected = state.selection.processor,
                toLabel = { it },
                onSelected = viewModel::onProcessorChanged
            )

            SelectableChips(
                title = "RAM",
                options = options.rams,
                selected = state.selection.ramGb,
                toLabel = { "$it GB" },
                onSelected = viewModel::onRamChanged
            )

            SelectableChips(
                title = "Almacenamiento",
                options = options.storages,
                selected = state.selection.storageGb,
                toLabel = { "$it GB" },
                onSelected = viewModel::onStorageChanged
            )

            SelectableChips(
                title = "Color",
                options = options.colors,
                selected = state.selection.color,
                toLabel = { it },
                onSelected = viewModel::onColorChanged
            )

            options.selectedVariant?.let {
                Text("Precio: USD ${it.price}", style = MaterialTheme.typography.titleMedium)
                Text("Stock disponible: ${it.stock}")
            }

            Button(onClick = onBack) {
                Text("Volver")
            }
        }
    }
}
