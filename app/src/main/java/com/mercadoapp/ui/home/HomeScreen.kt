package com.mercadoapp.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
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
import com.mercadoapp.domain.model.Product

@Composable
fun HomeRoute(
    onProductClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    HomeScreen(state = state, onProductClick = onProductClick)
}

@Composable
private fun HomeScreen(state: HomeUiState, onProductClick: (String) -> Unit) {
    Scaffold { padding ->
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Text("Ofertas", style = MaterialTheme.typography.headlineSmall) }
                items(state.offers) { ProductCard(it, onProductClick) }

                item { Text("Últimos equipos", style = MaterialTheme.typography.headlineSmall) }
                items(state.newArrivals) { ProductCard(it, onProductClick) }

                item { Text("Todos", style = MaterialTheme.typography.headlineSmall) }
                items(state.products) { ProductCard(it, onProductClick) }
            }
        }
    }
}

@Composable
private fun ProductCard(product: Product, onProductClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick(product.id) }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(product.name, style = MaterialTheme.typography.titleMedium)
            Text(product.shortDescription, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
