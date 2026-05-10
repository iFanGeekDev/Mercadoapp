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
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    
    HomeScreen(
        pagingItems = pagingItems,
        selectedCategory = selectedCategory,
        searchQuery = searchQuery,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onCategoryClick = viewModel::onCategorySelected,
        onProductClick = onProductClick,
        onCartClick = onCartClick, 
        onProfileClick = onProfileClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    pagingItems: LazyPagingItems<Product>,
    selectedCategory: String,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val allItems = (0 until pagingItems.itemCount).mapNotNull { pagingItems[it] }
    val offers = allItems.filter { it.isOffer }
    val categories = listOf("ALL", "PHONES", "LAPTOPS", "TABLETS", "AUDIO", "WEARABLES")

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        containerColor = Dark900,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onProfileClick() }) {
                    Box(
                        modifier = Modifier.size(44.dp).background(Brush.linearGradient(listOf(Brand500, Accent500)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, null, tint = Color.White)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Hola,", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text("Explorer", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Box(
                    modifier = Modifier.size(44.dp).background(Dark800, CircleShape).clickable { onCartClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ShoppingCart, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    ) { padding ->

        if (pagingItems.loadState.refresh is LoadState.Loading && searchQuery.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Brand500)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    placeholder = { Text("Buscar productos...", color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChanged("") }) {
                                Icon(Icons.Default.Close, null, tint = TextSecondary)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Dark800,
                        focusedContainerColor = Dark800,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Brand500
                    ),
                    singleLine = true
                )
            }

            // Categories
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories) { category ->
                        Surface(
                            color = if (category == selectedCategory) Brand500 else Dark800,
                            shape = RoundedCornerShape(20.dp),
                            onClick = { onCategoryClick(category) }
                        ) {
                            Text(
                                text = category,
                                color = if (category == selectedCategory) Color.White else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }

            // Shop our most wanted
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        "Compra lo más buscado", 
                        style = MaterialTheme.typography.titleLarge, 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White
                    )
                    Spacer(Modifier.height(16.dp))
                    
                    val gridItems = listOf(
                        MostWantedItem("Grandes ofertas", "https://img.freepik.com/free-vector/shopping-basket-full-items-vector-illustration_1284-72418.jpg", "offer"),
                        MostWantedItem("iPhone", "https://store.storeimages.cdn-apple.com/4668/as-images.apple.com/is/iphone-card-40-iphone15pro-202309?wid=680&hei=528&fmt=p-jpg&qlt=95&.v=1693086290141", "iPhone", "PHONES"),
                        MostWantedItem("MacBook", "https://store.storeimages.cdn-apple.com/4668/as-images.apple.com/is/macbook-air-silver-config-202403?wid=840&hei=508&fmt=jpeg&qlt=90&.v=1708451848316", "MacBook", "LAPTOPS"),
                        MostWantedItem("iPad", "https://store.storeimages.cdn-apple.com/4668/as-images.apple.com/is/ipad-card-40-ipad-202210?wid=680&hei=528&fmt=p-jpg&qlt=95&.v=1664402636515", "iPad", "TABLETS"),
                        MostWantedItem("Videojuegos", "https://m.media-amazon.com/images/I/5105Sog6YGL._AC_SL1500_.jpg", "consola"),
                        MostWantedItem("Android", "https://images.samsung.com/is/image/samsung/p6pim/pe/2401/gallery/pe-galaxy-s24-s928-sm-s928bztqpeo-539281788?$650_519_PNG$", "Samsung"),
                        MostWantedItem("Laptops Windows", "https://images-na.ssl-images-amazon.com/images/I/81sh9kn8oHL._AC_SL1500_.jpg", "Windows", "LAPTOPS"),
                        MostWantedItem("Airpods", "https://store.storeimages.cdn-apple.com/4668/as-images.apple.com/is/MME73?wid=1144&hei=1144&fmt=jpeg&qlt=95&.v=1632861342000", "Airpods", "AUDIO")
                    )

                    // 2x4 Grid using Rows and Columns for better control in LazyColumn
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        for (i in 0 until 4) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                val item1 = gridItems[i * 2]
                                val item2 = gridItems[i * 2 + 1]
                                
                                MostWantedCard(
                                    item = item1,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        onSearchQueryChanged(item1.searchQuery ?: "")
                                        item1.category?.let { onCategoryClick(it) }
                                    }
                                )
                                MostWantedCard(
                                    item = item2,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        onSearchQueryChanged(item2.searchQuery ?: "")
                                        item2.category?.let { onCategoryClick(it) }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Flash Deal
            if (offers.isNotEmpty()) {
                item {
                    Text("Oferta Relámpago", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 24.dp))
                    Spacer(Modifier.height(12.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(offers) { product ->
                            FlashDealCard(product, onClick = { onProductClick(product.id) })
                        }
                    }
                }
            }

            // For You Section
            item {
                Text("Para Ti", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 24.dp))
            }
            
            items(count = pagingItems.itemCount, key = pagingItems.itemKey { it.id }) { index ->
                val product = pagingItems[index]
                if (product != null && !product.isOffer) {
                    ForYouCard(product = product, onClick = { onProductClick(product.id) }, modifier = Modifier.padding(horizontal = 24.dp))
                }
            }
            
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun FlashDealCard(product: Product, onClick: () -> Unit) {
    val minPrice = product.variants.minOfOrNull { it.price } ?: 0.0
    Card(
        onClick = onClick,
        modifier = Modifier.width(280.dp).height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Dark800)
    ) {
        Box(Modifier.fillMaxSize()) {
            AsyncImage(model = product.imageUrl, contentDescription = product.name, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().align(Alignment.CenterEnd).offset(x = 40.dp))
            Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Dark900, Dark900.copy(alpha = 0.8f), Color.Transparent))))
            
            Column(modifier = Modifier.padding(20.dp).fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                Surface(color = Brand500, shape = RoundedCornerShape(6.dp)) {
                    Text("CYBER DAYS", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Spacer(Modifier.height(12.dp))
                Text(product.name, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
                Spacer(Modifier.height(4.dp))
                Text("$${"%.2f".format(minPrice)}", style = MaterialTheme.typography.titleLarge, color = Accent500, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun ForYouCard(product: Product, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val minPrice = product.variants.minOfOrNull { it.price } ?: 0.0
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Dark800)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(80.dp).background(Dark700, RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp))) {
                AsyncImage(model = product.imageUrl, contentDescription = product.name, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                Text(product.shortDescription, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(8.dp))
                Text("$${"%.2f".format(minPrice)}", style = MaterialTheme.typography.titleMedium, color = Accent500, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.FavoriteBorder, null, tint = TextSecondary)
        }
    }
}

data class MostWantedItem(
    val title: String,
    val imageUrl: String,
    val searchQuery: String? = null,
    val category: String? = null
)

@Composable
private fun MostWantedCard(
    item: MostWantedItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD4F467))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxHeight().padding(bottom = 8.dp)
                )
            }
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF1A1A1A),
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
