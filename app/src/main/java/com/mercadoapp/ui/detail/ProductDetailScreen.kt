package com.mercadoapp.ui.detail

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mercadoapp.ui.components.CartBadgeIcon
import com.mercadoapp.ui.components.QuantitySelector
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
    val cartCount by viewModel.cartCount.collectAsState()
    
    ProductDetailScreen(
        state = state, 
        cartCount = cartCount,
        onBack = onBack, 
        onCartClick = onCartClick, 
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailScreen(
    state: ProductDetailUiState,
    cartCount: Int,
    onBack: () -> Unit,
    onCartClick: () -> Unit,
    viewModel: ProductDetailViewModel
) {
    val options = state.optionsState ?: EmptyOptions

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
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconButton(
                        onClick = { viewModel.toggleFavorite() },
                        modifier = Modifier.background(Dark800, CircleShape)
                    ) {
                        Icon(
                            if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (state.isFavorite) Color.Red else Color.White
                        )
                    }
                    IconButton(onClick = onCartClick, modifier = Modifier.background(Dark800, CircleShape)) {
                        CartBadgeIcon(icon = Icons.Default.ShoppingCart, count = cartCount)
                    }
                }
            }
        },
        bottomBar = {
            if (options.selectedVariant != null) {
                val variant = options.selectedVariant
                Surface(color = Dark800, border = BorderStroke(1.dp, Dark700)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Precio Total", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("$${"%.2f".format(variant.price * state.quantity)}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }
                            QuantitySelector(
                                quantity = state.quantity,
                                onQuantityChange = { viewModel.onQuantityChanged(it) }
                            )
                        }
                        
                        Spacer(Modifier.height(12.dp))
                        
                        Box(modifier = Modifier.fillMaxWidth().height(52.dp).background(Brush.horizontalGradient(listOf(Brand600, Brand400)), RoundedCornerShape(12.dp))) {
                            Button(
                                onClick = { viewModel.addToCart() },
                                enabled = variant.stock >= state.quantity && !state.cartAdded,
                                modifier = Modifier.fillMaxSize(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(if (state.cartAdded) "En el Carrito" else "Añadir al Carrito", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->

        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Brand500) }
            return@Scaffold
        }

        val product = state.product ?: return@Scaffold

        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            // Product Image Hero
            Box(modifier = Modifier.fillMaxWidth().height(360.dp).padding(horizontal = 24.dp)) {
                AsyncImage(model = product.imageUrl, contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize())
            }

            // Product Details Card
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp), color = Dark800) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Star, null, tint = Accent500, modifier = Modifier.size(16.dp))
                                Text("4.9", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text("(1,284 reseñas)", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    Text(product.shortDescription, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, lineHeight = 24.sp)

                    HorizontalDivider(color = Dark700)

                    Text("Configura tu equipo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)

                    SelectableChips(title = "Condición", options = options.conditions, selected = state.selection.condition, toLabel = { it.name }, onSelected = viewModel::onConditionChanged)
                    SelectableChips(title = "Procesador", options = options.processors, selected = state.selection.processor, toLabel = { it }, onSelected = viewModel::onProcessorChanged)
                    SelectableChips(title = "RAM", options = options.rams, selected = state.selection.ramGb, toLabel = { "$it GB" }, onSelected = viewModel::onRamChanged)
                    SelectableChips(title = "Almacenamiento", options = options.storages, selected = state.selection.storageGb, toLabel = { "$it GB" }, onSelected = viewModel::onStorageChanged)
                    SelectableChips(title = "Color", options = options.colors, selected = state.selection.color, toLabel = { it }, onSelected = viewModel::onColorChanged)

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}
