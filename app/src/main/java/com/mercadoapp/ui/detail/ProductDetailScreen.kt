package com.mercadoapp.ui.detail

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mercadoapp.ui.components.SelectableChips
import com.mercadoapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailRoute(
    onBack: () -> Unit,
    onCartClick: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    ProductDetailScreen(state = state, onBack = onBack, onCartClick = onCartClick, viewModel = viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailScreen(
    state: ProductDetailUiState,
    onBack: () -> Unit,
    onCartClick: () -> Unit,
    viewModel: ProductDetailViewModel
) {
    val options = state.optionsState ?: EmptyOptions

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Dark700.copy(alpha = 0.8f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ArrowBack, null,
                                modifier = Modifier.size(20.dp), tint = TextPrimary)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onCartClick) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Brand500.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ShoppingCart, null,
                                modifier = Modifier.size(20.dp), tint = Brand400)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = options.selectedVariant != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                val variant = options.selectedVariant
                if (variant != null) {
                    Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Precio total", style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("USD ${"%.2f".format(variant.price)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (state.product?.isOffer == true) OfferBadge
                                    else MaterialTheme.colorScheme.primary)
                                Text(if (variant.stock > 0) "Stock: ${variant.stock} unidades"
                                else "Sin stock",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (variant.stock > 0) Accent500 else MaterialTheme.colorScheme.error)
                            }
                            Button(
                                onClick = { viewModel.addToCart() },
                                enabled = variant.stock > 0 && !state.cartAdded,
                                modifier = Modifier.height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (state.cartAdded) Accent500 else Brand500,
                                    disabledContainerColor = Dark500
                                )
                            ) {
                                AnimatedContent(targetState = state.cartAdded, label = "cart_btn") { added ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically) {
                                        Icon(if (added) Icons.Default.Check else Icons.Default.ShoppingCart,
                                            null, modifier = Modifier.size(18.dp))
                                        Text(if (added) "Agregado" else "Agregar",
                                            fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->

        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            return@Scaffold
        }

        val product = state.product ?: run {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.ErrorOutline, null, Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Producto no encontrado", style = MaterialTheme.typography.titleMedium)
                    OutlinedButton(onClick = onBack) { Text("Volver") }
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ── Hero image with gradient ───────────────────────────────────────
            Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Gradient overlay top → transparent (for navbar visibility)
                Box(modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        listOf(Dark900.copy(alpha = 0.35f), Color.Transparent, Dark900.copy(alpha = 0.5f))
                    )
                ))
                // Offer badge
                if (product.isOffer) {
                    Surface(
                        color = OfferBadge,
                        shape = RoundedCornerShape(bottomEnd = 12.dp),
                        modifier = Modifier.align(Alignment.TopStart).offset(y = padding.calculateTopPadding())
                    ) {
                        Text("OFERTA", style = MaterialTheme.typography.labelMedium,
                            color = Color.White, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                    }
                }
            }

            // ── Content card ──────────────────────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Name & description
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(product.name, style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold)
                        Text(product.shortDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    // ── Options ───────────────────────────────────────────────
                    Text("Configurá tu equipo", style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    SelectableChips(title = "Condición", options = options.conditions,
                        selected = state.selection.condition,
                        toLabel = { it.name.lowercase().replaceFirstChar(Char::uppercaseChar) },
                        onSelected = viewModel::onConditionChanged)

                    SelectableChips(title = "Procesador", options = options.processors,
                        selected = state.selection.processor,
                        toLabel = { it }, onSelected = viewModel::onProcessorChanged)

                    SelectableChips(title = "RAM", options = options.rams,
                        selected = state.selection.ramGb,
                        toLabel = { "$it GB" }, onSelected = viewModel::onRamChanged)

                    SelectableChips(title = "Almacenamiento", options = options.storages,
                        selected = state.selection.storageGb,
                        toLabel = { "$it GB" }, onSelected = viewModel::onStorageChanged)

                    SelectableChips(title = "Color", options = options.colors,
                        selected = state.selection.color,
                        toLabel = { it }, onSelected = viewModel::onColorChanged)

                    // Hint when no variant selected
                    if (options.selectedVariant == null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Dark600, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, null, modifier = Modifier.size(16.dp),
                                tint = Brand400)
                            Text("Seleccioná todas las opciones para ver el precio",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary)
                        }
                    }

                    // Add-to-cart success snackbar (inline)
                    AnimatedVisibility(visible = state.cartAdded,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Accent500.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(18.dp),
                                tint = Accent500)
                            Text("¡Producto agregado al carrito!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Accent400, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = onCartClick,
                                contentPadding = PaddingValues(0.dp)) {
                                Text("Ver carrito", style = MaterialTheme.typography.labelSmall,
                                    color = Accent500)
                            }
                        }
                    }

                    Spacer(Modifier.height(80.dp)) // room for bottom bar
                }
            }
        }
    }
}
