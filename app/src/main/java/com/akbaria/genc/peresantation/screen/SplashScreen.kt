package com.akbaria.genc.peresantation.screen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navigateToLogin: () -> Unit,
    navigateToBuyer: () -> Unit,
    navigateToSeller: () -> Unit,
    isAuthenticated: Boolean,
    isSeller: Boolean
) {
    LaunchedEffect(Unit) {
        delay(2000) // 2 saniyə gözlə
        if (isAuthenticated) {
            if (isSeller) navigateToSeller() else navigateToBuyer()
        } else {
            navigateToLogin()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Art Marketplace", style = MaterialTheme.typography.displayLarge)
    }
}