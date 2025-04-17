package com.akbaria.genc.peresantation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object Buyer : Screen("buyer_screen")
    object Seller : Screen("seller_screen")
    object AddArtwork : Screen("add_artwork_screen")
}