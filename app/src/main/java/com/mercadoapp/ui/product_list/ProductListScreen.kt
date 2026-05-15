package com.mercadoapp.ui.product_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.mercadoapp.ui.components.CartBadgeIcon
import com.mercadoapp.domain.model.Product
import com.mercadoapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListRoute(
    onBack: () -> Unit,
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit,
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val pagingItems = viewModel.productsPaged.collectAsLazyPagingItems()
    val cartCount by viewModel.cartCount.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val title = if (viewModel.searchQuery.isNotBlank()) viewModel.searchQuery else viewModel.category ?: "Productos"

    ProductListScreen(
        pagingItems = pagingItems,
        cartCount = cartCount,
        favoriteIds = favoriteIds,
        title = title,
        onBack = onBack,
        onProductClick = onProductClick,
        onFavoriteClick = viewModel::toggleFavorite,
        onCartClick = onCartClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductListScreen(
    pagingItems: androidx.paging.compose.LazyPagingItems<Product>,
    cartCount: Int,
    favoriteIds: Set<String>,
    title: String,
    onBack: () -> Unit,
    onProductClick: (String) -> Unit,
    onFavoriteClick: (Product) -> Unit,
    onCartClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
        },
        containerColor = Dark900
    ) { padding ->
        if (pagingItems.loadState.refresh is LoadState.Loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Brand500)
            }
        } else if (pagingItems.itemCount == 0) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No hay productos", color = TextSecondary)
                    Text("para mostrar en esta sección.", color = TextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(count = pagingItems.itemCount, key = pagingItems.itemKey { it.id }) { index ->
                    val product = pagingItems[index]
                    if (product != null) {
                        ProductListItem(
                            product = product, 
                            isFavorite = favoriteIds.contains(product.id),
                            onClick = { onProductClick(product.id) },
                            onFavoriteClick = { onFavoriteClick(product) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductListItem(product: Product, isFavorite: Boolean, onClick: () -> Unit, onFavoriteClick: () -> Unit) {
    val minPrice = product.variants.minOfOrNull { it.price } ?: 0.0
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Dark800)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(80.dp).background(Dark700, androidx.compose.foundation.shape.RoundedCornerShape(16.dp)).clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))) {
                SubcomposeAsyncImage(
                    model = product.imageUrl, 
                    contentDescription = product.name, 
                    contentScale = ContentScale.Crop, 
                    modifier = Modifier.fillMaxSize(),
                    loading = { Box(Modifier.fillMaxSize().background(Dark800)) },
                    error = {
                        Box(Modifier.fillMaxSize().background(Dark800), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.BrokenImage, null, tint = TextSecondary)
                        }
                    }
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                Text(product.shortDescription, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(8.dp))
                Text("$${"%.2f".format(minPrice)}", style = MaterialTheme.typography.titleMedium, color = Accent500, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onFavoriteClick) {
                Icon(if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null, tint = if (isFavorite) Brand500 else TextSecondary)
            }
        }
    }
}
