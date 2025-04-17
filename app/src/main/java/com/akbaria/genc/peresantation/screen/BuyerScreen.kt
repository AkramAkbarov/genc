package com.akbaria.genc.peresantation.screen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.akbaria.genc.domain.model.Artwork
import com.akbaria.genc.peresantation.viewmodel.ArtBuyerViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerScreen(
    navigateToLogin: () -> Unit,
    viewModel: ArtBuyerViewModel = hiltViewModel()
) {
    val artworksState by viewModel.artworksState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Bütün Əsərlər") },
            actions = {
                IconButton(onClick = navigateToLogin) {
                    Icon(Icons.Default.Logout, "Çıxış")
                }
            }
        )

        when {
            artworksState.isLoading -> CircularProgressIndicator()
            artworksState.error.isNotEmpty() -> Text(artworksState.error)
            else -> LazyColumn {
                items(artworksState.artworks) { artwork ->
                    ArtworkItem(artwork = artwork)
                }
            }
        }
    }
}

@Composable
fun ArtworkItem(artwork: Artwork) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = artwork.title, style = MaterialTheme.typography.titleLarge)
            Text(text = artwork.description)
            Text(text = "Qiymət: ${artwork.price} AZN")
            Text(text = "Satıcı: ${artwork.sellerName}")
        }
    }
}