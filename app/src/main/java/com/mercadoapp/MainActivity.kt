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
import com.mercadoapp.ui.auth.RegisterRoute
import com.mercadoapp.ui.cart.CartRoute
import com.mercadoapp.ui.checkout.CheckoutRoute
import com.mercadoapp.ui.detail.ProductDetailRoute
import com.mercadoapp.ui.home.HomeRoute
import com.mercadoapp.ui.profile.ProfileRoute
import com.mercadoapp.ui.address.AddressEditRoute
import com.mercadoapp.ui.address.AddressListRoute
import com.mercadoapp.ui.order.OrderHistoryRoute
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

    // Reactive auth guard
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Unauthenticated -> {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AuthState.Authenticated -> {
                if (navController.currentDestination?.route == "login" ||
                    navController.currentDestination?.route == "register"
                ) {
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            else -> { /* Loading */ }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (authState is AuthState.Authenticated) "home" else "login"
    ) {
        // ── Auth ──────────────────────────────────────────────────────────────
        composable("login") {
            LoginRoute(
                onLoginSuccess = {
                    navController.navigate("home") { popUpTo("login") { inclusive = true } }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            RegisterRoute(
                onRegisterSuccess = {
                    navController.navigate("home") { popUpTo(0) { inclusive = true } }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // ── Main ──────────────────────────────────────────────────────────────
        composable("home") {
            HomeRoute(
                onProductClick = { id -> navController.navigate("detail/$id") },
                onCartClick    = { navController.navigate("cart") },
                onProfileClick = { navController.navigate("profile") }
            )
        }

        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            ProductDetailRoute(
                onBack      = { navController.popBackStack() },
                onCartClick = { navController.navigate("cart") }
            )
        }

        composable("cart") {
            CartRoute(
                onBack     = { navController.popBackStack() },
                onCheckout = { navController.navigate("checkout") }
            )
        }

        composable("checkout") {
            CheckoutRoute(
                onBack = { navController.popBackStack() },
                onPaymentSuccess = {
                    navController.navigate("home") { popUpTo("home") { inclusive = true } }
                },
                onChangeAddress = { navController.navigate("address_list") }
            )
        }

        composable("profile") {
            ProfileRoute(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                },
                onAddressesClick = { navController.navigate("address_list") },
                onOrdersClick = { navController.navigate("order_history") }
            )
        }

        composable("order_history") {
            OrderHistoryRoute(
                onBack = { navController.popBackStack() }
            )
        }

        composable("address_list") {
            AddressListRoute(
                onBack = { navController.popBackStack() },
                onAddAddress = { navController.navigate("address_edit/new") },
                onEditAddress = { id -> navController.navigate("address_edit/$id") }
            )
        }

        composable(
            route = "address_edit/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            AddressEditRoute(
                onBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
    }
}
