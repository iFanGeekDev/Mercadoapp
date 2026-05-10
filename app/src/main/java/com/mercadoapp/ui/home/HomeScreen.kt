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
                        MostWantedItem("Grandes ofertas", "https://upload.wikimedia.org/wikipedia/commons/thumb/1/14/Shopping_cart_icon.png/512px-Shopping_cart_icon.png", "offer"),
                        MostWantedItem("iPhone", "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b3/IPhone_13_Pro_Vector.svg/512px-IPhone_13_Pro_Vector.svg.png", "iPhone", "PHONES"),
                        MostWantedItem("MacBook", "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c9/MacBook_Pro_16-inch_2021_Space_Gray_with_macOS_Monterey.png/512px-MacBook_Pro_16-inch_2021_Space_Gray_with_macOS_Monterey.png", "MacBook", "LAPTOPS"),
                        MostWantedItem("iPad", "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/IPad_Air_4th_Gen_Space_Gray.png/512px-IPad_Air_4th_Gen_Space_Gray.png", "iPad", "TABLETS"),
                        MostWantedItem("Videojuegos", "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1b/PlayStation_5_and_DualSense_with_transparent_background.png/512px-PlayStation_5_and_DualSense_with_transparent_background.png", "consola"),
                        MostWantedItem("Android", "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8d/Samsung_Galaxy_S23_Ultra_Cream.png/512px-Samsung_Galaxy_S23_Ultra_Cream.png", "Samsung"),
                        MostWantedItem("Laptops Windows", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/Dell_XPS_13_9310_Touch.png/512px-Dell_XPS_13_9310_Touch.png", "Windows", "LAPTOPS"),
                        MostWantedItem("Airpods", "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e7/AirPods_Pro.png/512px-AirPods_Pro.png", "Airpods", "AUDIO")
                    )

                    // Horizontal scrollable list
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(gridItems) { item ->
                            MostWantedCard(
                                item = item,
                                modifier = Modifier.width(140.dp),
                                onClick = {
                                    onSearchQueryChanged(item.searchQuery ?: "")
                                    item.category?.let { onCategoryClick(it) }
                                }
                            )
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
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        colors = CardDefaults.cardColors(containerColor = Dark800)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Subtle glow behind the image
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(Brand500.copy(alpha = 0.1f), Color.Transparent)
                            ),
                            CircleShape
                        )
                )
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
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
