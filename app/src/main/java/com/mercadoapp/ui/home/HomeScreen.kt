package com.mercadoapp.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.mercadoapp.domain.model.Product
import com.mercadoapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val pagingItems = viewModel.productsPaged.collectAsLazyPagingItems()
    HomeScreen(pagingItems = pagingItems, onProductClick = onProductClick,
        onCartClick = onCartClick, onProfileClick = onProfileClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    pagingItems: LazyPagingItems<Product>,
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val allItems = (0 until pagingItems.itemCount).mapNotNull { pagingItems[it] }
    val offers      = allItems.filter { it.isOffer }
    val newArrivals = allItems.filter { it.isNewArrival }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Brand500, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("M", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        }
                        Text("MercadoApp", fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onCartClick) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito",
                            tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->

        when {
            pagingItems.loadState.refresh is LoadState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            pagingItems.loadState.refresh is LoadState.Error -> {
                val e = (pagingItems.loadState.refresh as LoadState.Error).error
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.WifiOff, null, modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Sin conexión", style = MaterialTheme.typography.titleMedium)
                        Text(e.localizedMessage ?: "", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Button(onClick = { pagingItems.retry() }) { Text("Reintentar") }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // ── Hero Banner ────────────────────────────────────────────
                    if (offers.isNotEmpty()) {
                        item {
                            SectionHeader(title = "🔥 Ofertas del día", emoji = true)
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(offers) { product ->
                                    HeroProductCard(product = product, onClick = { onProductClick(product.id) })
                                }
                            }
                            Spacer(Modifier.height(24.dp))
                        }
                    }

                    // ── New Arrivals ───────────────────────────────────────────
                    if (newArrivals.isNotEmpty()) {
                        item {
                            SectionHeader(title = "✨ Últimos equipos", emoji = true)
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(newArrivals) { product ->
                                    CompactProductCard(product = product, onClick = { onProductClick(product.id) })
                                }
                            }
                            Spacer(Modifier.height(24.dp))
                        }
                    }

                    // ── All Products ───────────────────────────────────────────
                    item { SectionHeader(title = "Todos los equipos") }
                    items(
                        count = pagingItems.itemCount,
                        key   = pagingItems.itemKey { it.id }
                    ) { index ->
                        val product = pagingItems[index]
                        if (product != null) {
                            ListProductCard(
                                product = product,
                                onClick = { onProductClick(product.id) },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        } else {
                            ListProductCardSkeleton(Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
                        }
                    }

                    if (pagingItems.loadState.append is LoadState.Loading) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(28.dp),
                                    strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// ── Section Header ─────────────────────────────────────────────────────────────
@Composable
private fun SectionHeader(title: String, emoji: Boolean = false) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

// ── Hero Card (wide, for offers carousel) ─────────────────────────────────────
@Composable
private fun HeroProductCard(product: Product, onClick: () -> Unit) {
    val minPrice = product.variants.minOfOrNull { it.price }
    Card(
        onClick = onClick,
        modifier = Modifier.width(240.dp).height(200.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(Modifier.fillMaxSize()) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xCC0D0D14)),
                            startY = 80f
                        )
                    )
            )
            // Content
            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    color = OfferBadge,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("OFERTA", style = MaterialTheme.typography.labelSmall,
                        color = Color.White, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
                Text(product.name, style = MaterialTheme.typography.titleSmall,
                    color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
                if (minPrice != null) {
                    Text("Desde USD ${"%.0f".format(minPrice)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Accent400, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ── Compact Card (for new arrivals carousel) ───────────────────────────────────
@Composable
private fun CompactProductCard(product: Product, onClick: () -> Unit) {
    val minPrice = product.variants.minOfOrNull { it.price }
    Card(
        onClick = onClick,
        modifier = Modifier.width(150.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(product.name, style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (minPrice != null) {
                    Text("USD ${"%.0f".format(minPrice)}",
                        style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

// ── List Card (vertical list) ──────────────────────────────────────────────────
@Composable
fun ListProductCard(product: Product, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val minPrice = product.variants.minOfOrNull { it.price }
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(80.dp).clip(RoundedCornerShape(12.dp))) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    if (product.isOffer) {
                        Surface(color = OfferBadge.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                            Text("OFERTA", style = MaterialTheme.typography.labelSmall,
                                color = OfferBadge, fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp))
                        }
                    }
                    if (product.isNewArrival) {
                        Surface(color = Accent500.copy(alpha = 0.12f), shape = RoundedCornerShape(4.dp)) {
                            Text("NUEVO", style = MaterialTheme.typography.labelSmall,
                                color = Accent500, fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp))
                        }
                    }
                }
                Text(product.name, style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(product.shortDescription, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2,
                    overflow = TextOverflow.Ellipsis)
                if (minPrice != null) {
                    Text("Desde USD ${"%.0f".format(minPrice)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold)
                }
            }
            Icon(Icons.Default.ChevronRight, null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
        }
    }
}

// ── Skeleton placeholder ───────────────────────────────────────────────────────
@Composable
private fun ListProductCardSkeleton(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "shimmer_alpha"
    )
    Card(modifier = modifier.fillMaxWidth().height(96.dp), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha)
        )
    ) {}
}
