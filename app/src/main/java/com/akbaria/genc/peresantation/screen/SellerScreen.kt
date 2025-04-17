package com.akbaria.genc.peresantation.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.akbaria.genc.domain.model.Artwork
import com.akbaria.genc.peresantation.viewmodel.ArtSellerViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.akbaria.genc.R

private const val TAG = "SellerScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerScreen(
    navigateToAddArtwork: () -> Unit,
    navigateToLogin: () -> Unit,
    viewModel: ArtSellerViewModel = hiltViewModel()
) {
    val artworkState by viewModel.artworkState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eserlerim") },
                actions = {
                    IconButton(onClick = navigateToLogin) {
                        Icon(Icons.Default.Logout, "Çıkış")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = navigateToAddArtwork) {
                Icon(Icons.Default.Add, "Eser Ekle")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when {
                artworkState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                artworkState.error.isNotEmpty() -> {
                    Text(
                        text = artworkState.error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                artworkState.artworks.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Henüz eser eklenmemiş. Eklemek için + düğmesine tıklayın.")
                    }
                }
                else -> {
                    Log.d(TAG, "Artworks list size: ${artworkState.artworks.size}")
                    Log.d(TAG, "First artwork imageUrl: ${artworkState.artworks.firstOrNull()?.imageUrl}")

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(artworkState.artworks) { artwork ->
                            Log.d(TAG, "Displaying artwork: ${artwork.id}, imageUrl: ${artwork.imageUrl}")
                            SellerArtworkItem(
                                artwork = artwork,
                                onDelete = { viewModel.deleteArtwork(artwork.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SellerArtworkItem(artwork: Artwork, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (artwork.imageUrl.isNotBlank()) {
                val imageUrl = artwork.imageUrl
                Log.d(TAG, "Loading image from URL: $imageUrl")

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(artwork.imageUrl)
                        .crossfade(true)
                        .placeholder(R.drawable.ic_launcher_background) // Yüklenirken gösterilecek resim
                        .error(R.drawable.ic_launcher_foreground) // Hata durumunda gösterilecek resim
                        .build(),
                    contentDescription = artwork.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(text = artwork.title, style = MaterialTheme.typography.titleLarge)
            Text(text = artwork.description)
            Text(text = "Fiyat: ${artwork.price} ₺")

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text("Sil")
            }
        }
    }
}