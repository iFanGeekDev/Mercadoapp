package com.mercadoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mercadoapp.domain.model.AuthState
import com.mercadoapp.domain.repository.AuthRepository
import com.mercadoapp.ui.auth.LoginRoute
import com.mercadoapp.ui.cart.CartRoute
import com.mercadoapp.ui.detail.ProductDetailRoute
import com.mercadoapp.ui.home.HomeRoute
import com.mercadoapp.ui.theme.MercadoTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MercadoTheme {
                MercadoAppContent(authRepository = authRepository)
            }
        }
    }
}

@Composable
private fun MercadoAppContent(authRepository: AuthRepository) {
    val navController = rememberNavController()
    val authState by authRepository.authState.collectAsState(initial = AuthState.Loading)

    // Redirect to login/home depending on auth state
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Unauthenticated -> {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AuthState.Authenticated -> {
                if (navController.currentDestination?.route == "login") {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
            else -> { /* Loading — do nothing */ }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (authState is AuthState.Authenticated) "home" else "login"
    ) {
        composable("login") {
            LoginRoute(onLoginSuccess = {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }

        composable("home") {
            HomeRoute(
                onProductClick = { id -> navController.navigate("detail/$id") },
                onCartClick = { navController.navigate("cart") }
            )
        }

        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            ProductDetailRoute(
                onBack = { navController.popBackStack() },
                onCartClick = { navController.navigate("cart") }
            )
        }

        composable("cart") {
            CartRoute(
                onBack = { navController.popBackStack() },
                onCheckout = {
                    // TODO: navigate to CheckoutScreen when implemented
                    navController.popBackStack()
                }
            )
        }
    }
}
