package com.akbaria.genc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.akbaria.genc.peresantation.navigation.AppNavigation
import com.akbaria.genc.peresantation.viewmodel.theme.GencTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Bu, əgər edge-to-edge istifadə olunacaqsa, əmin olmaq üçün buradadır
        WindowCompat.setDecorFitsSystemWindows(window, false) // Edge-to-edge tam aktivləşdirilsin

        setContent {
            GencTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}