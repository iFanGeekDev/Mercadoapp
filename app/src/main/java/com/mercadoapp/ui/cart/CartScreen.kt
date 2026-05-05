package com.mercadoapp.ui.cart

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mercadoapp.domain.model.CartItem
import com.mercadoapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartRoute(
    onBack: () -> Unit,
    onCheckout: () -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    CartScreen(
        state = state, 
        onBack = onBack, 
        onCheckout = onCheckout, 
        onRemoveItem = viewModel::removeItem,
        onUpdateQuantity = viewModel::updateQuantity
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartScreen(
    state: CartUiState,
    onBack: () -> Unit,
    onCheckout: () -> Unit,
    onRemoveItem: (Int) -> Unit,
    onUpdateQuantity: (Int, Int) -> Unit
) {
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
                Text("My Cart", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Box(modifier = Modifier.size(48.dp)) // Spacer for alignment
            }
        },
        bottomBar = {
            if (state.items.isNotEmpty()) {
                Surface(color = Dark800, shadowElevation = 16.dp, shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Text("$${"%.2f".format(state.total)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Shipping", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Text("Free", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Accent500)
                        }
                        HorizontalDivider(color = Dark700)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                            Text("$${"%.2f".format(state.total)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Brand500)
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(56.dp).background(Brush.horizontalGradient(listOf(Brand600, Brand400)), RoundedCornerShape(16.dp))) {
                            Button(
                                onClick = onCheckout,
                                modifier = Modifier.fillMaxSize(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("CHECKOUT", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->

        if (state.items.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(64.dp), tint = Dark700)
                    Text("Your cart is empty", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.items, key = { it.id }) { item ->
                CartItemCard(
                    item = item,
                    onRemove = { onRemoveItem(item.id) },
                    onIncrease = { if (item.quantity < item.variant.stock) onUpdateQuantity(item.id, item.quantity + 1) },
                    onDecrease = { if (item.quantity > 1) onUpdateQuantity(item.id, item.quantity - 1) }
                )
            }
        }
    }
}

@Composable
private fun CartItemCard(item: CartItem, onRemove: () -> Unit, onIncrease: () -> Unit, onDecrease: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Dark800)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(80.dp).background(Dark700, RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp))) {
                AsyncImage(model = item.productImageUrl, contentDescription = item.productName, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.productName, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconButton(onClick = onDecrease, modifier = Modifier.size(28.dp).background(Dark700, CircleShape)) {
                        Icon(Icons.Default.Remove, null, tint = if (item.quantity > 1) Color.White else Dark400, modifier = Modifier.size(16.dp))
                    }
                    Text("${item.quantity}", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    IconButton(onClick = onIncrease, modifier = Modifier.size(28.dp).background(Dark700, CircleShape)) {
                        Icon(Icons.Default.Add, null, tint = if (item.quantity < item.variant.stock) Color.White else Dark400, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("$${"%.2f".format(item.variant.price * item.quantity)}", style = MaterialTheme.typography.titleMedium, color = Brand500, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onRemove, modifier = Modifier.background(Dark700, CircleShape).size(36.dp)) {
                Icon(Icons.Default.Delete, null, tint = ErrorRed, modifier = Modifier.size(18.dp))
            }
        }
    }
}
