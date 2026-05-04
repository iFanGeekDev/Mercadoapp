package com.mercadoapp.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mercadoapp.ui.components.SelectableChips

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailRoute(
    onBack: () -> Unit,
    onCartClick: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    ProductDetailScreen(
        state = state,
        onBack = onBack,
        onCartClick = onCartClick,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailScreen(
    state: ProductDetailUiState,
    onBack: () -> Unit,
    onCartClick: () -> Unit,
    viewModel: ProductDetailViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.product?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onCartClick) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                    }
                }
            )
        }
    ) { padding ->

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        val product = state.product ?: run {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("Producto no encontrado") }
            return@Scaffold
        }

        val options = state.optionsState ?: EmptyOptions

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Product image
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            )

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    product.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    product.shortDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

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

                Spacer(Modifier.height(8.dp))

                options.selectedVariant?.let { variant ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Precio", style = MaterialTheme.typography.labelMedium)
                                Text(
                                    "USD ${"%.2f".format(variant.price)}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                "Stock: ${variant.stock}",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (variant.stock > 0) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Button(
                        onClick = { viewModel.addToCart() },
                        enabled = variant.stock > 0 && state.cartAdded.not(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (state.cartAdded) "¡Agregado!" else "Agregar al carrito",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                } ?: run {
                    Text(
                        "Seleccioná todas las opciones para ver el precio",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
