package com.akbaria.genc.peresantation.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.akbaria.genc.peresantation.screen.AddArtworkScreen
import com.akbaria.genc.peresantation.screen.BuyerScreen
import com.akbaria.genc.peresantation.screen.LoginScreen
import com.akbaria.genc.peresantation.screen.RegisterScreen
import com.akbaria.genc.peresantation.screen.SellerScreen
import com.akbaria.genc.peresantation.screen.SplashScreen
import com.akbaria.genc.peresantation.viewmodel.AuthViewModel


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()
    val userState by authViewModel.userState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(
                navigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                navigateToBuyer = {
                    navController.navigate(Screen.Buyer.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                navigateToSeller = {
                    navController.navigate(Screen.Seller.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                isAuthenticated = authState.isAuthenticated,
                isSeller = userState.user?.isSeller ?: false
            )
        }

        composable(route = Screen.Login.route) {
            LoginScreen(
                navigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                navigateToBuyer = {
                    navController.navigate(Screen.Buyer.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                navigateToSeller = {
                    navController.navigate(Screen.Seller.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Register.route) {
            RegisterScreen(
                navigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                navigateToBuyer = {
                    navController.navigate(Screen.Buyer.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                navigateToSeller = {
                    navController.navigate(Screen.Seller.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Buyer.route) {
            BuyerScreen(
                navigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Buyer.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Seller.route) {
            SellerScreen(
                navigateToAddArtwork = {
                    navController.navigate(Screen.AddArtwork.route)
                },
                navigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Seller.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.AddArtwork.route) {
            AddArtworkScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}