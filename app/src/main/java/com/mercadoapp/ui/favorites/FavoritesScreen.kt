package com.mercadoapp.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mercadoapp.ui.components.CartBadgeIcon
import com.mercadoapp.domain.model.Product
import com.mercadoapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesRoute(
    onBack: () -> Unit,
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val cartCount by viewModel.cartCount.collectAsState()
    
    FavoritesScreen(
        state = state, 
        cartCount = cartCount,
        onBack = onBack, 
        onProductClick = onProductClick,
        onCartClick = onCartClick,
        onRemoveFavorite = { viewModel.removeFavorite(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesScreen(
    state: FavoritesUiState,
    cartCount: Int,
    onBack: () -> Unit,
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onRemoveFavorite: (String) -> Unit
) {
    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        containerColor = Dark900,
        topBar = {
            TopAppBar(
                title = { Text("Mis Favoritos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onCartClick) {
                        CartBadgeIcon(icon = Icons.Default.ShoppingCart, count = cartCount)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Dark900,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Brand500)
            }
        } else if (state.products.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Favorite, null, modifier = Modifier.size(64.dp), tint = Dark700)
                    Spacer(Modifier.height(16.dp))
                    Text("Aún no tienes favoritos", color = TextSecondary, fontSize = 18.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.products, key = { it.id }) { product ->
                    FavoriteProductCard(
                        product = product,
                        onClick = { onProductClick(product.id) },
                        onRemove = { onRemoveFavorite(product.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteProductCard(
    product: Product,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    val minPrice = product.variants.minOfOrNull { it.price } ?: 0.0
    val totalStock = product.variants.sumOf { it.stock }
    val isOutOfStock = totalStock <= 0

    Card(
        onClick = if (isOutOfStock) ({}) else onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOutOfStock) Dark800.copy(alpha = 0.5f) else Dark800
        ),
        enabled = !isOutOfStock
    ) {
        Row(
            modifier = Modifier.padding(16.dp), 
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(80.dp).background(Dark700, RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp))) {
                AsyncImage(
                    model = product.imageUrl, 
                    contentDescription = product.name, 
                    contentScale = ContentScale.Crop, 
                    modifier = Modifier.fillMaxSize(),
                    alpha = if (isOutOfStock) 0.5f else 1f
                )
                if (isOutOfStock) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "AGOTADO", 
                            color = Color.White, 
                            fontSize = 10.sp, 
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    product.name, 
                    style = MaterialTheme.typography.titleMedium, 
                    color = if (isOutOfStock) Color.Gray else Color.White, 
                    fontWeight = FontWeight.Bold, 
                    maxLines = 1, 
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    product.shortDescription, 
                    style = MaterialTheme.typography.bodySmall, 
                    color = TextSecondary, 
                    maxLines = 1, 
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                if (isOutOfStock) {
                    Text(
                        "No disponible", 
                        style = MaterialTheme.typography.bodyMedium, 
                        color = MaterialTheme.colorScheme.error, 
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        "$${"%.2f".format(minPrice)}", 
                        style = MaterialTheme.typography.titleMedium, 
                        color = Accent500, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
            }
        }
    }
}
