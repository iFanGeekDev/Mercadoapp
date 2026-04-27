package com.mercadoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mercadoapp.ui.detail.ProductDetailRoute
import com.mercadoapp.ui.home.HomeRoute
import com.mercadoapp.ui.theme.MercadoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MercadoAppContent() }
    }
}

@Composable
private fun MercadoAppContent() {
    MercadoTheme {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeRoute(onProductClick = { id -> navController.navigate("detail/$id") })
            }
            composable(
                route = "detail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) {
                ProductDetailRoute(onBack = { navController.popBackStack() })
            }
        }
    }
}
