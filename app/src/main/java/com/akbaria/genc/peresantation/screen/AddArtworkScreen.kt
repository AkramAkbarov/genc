package com.akbaria.genc.peresantation.screen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.akbaria.genc.domain.model.Artwork
import com.akbaria.genc.peresantation.viewmodel.ArtSellerViewModel

private const val TAG = "AddArtworkScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddArtworkScreen(
    onNavigateBack: () -> Unit,
    viewModel: ArtSellerViewModel = hiltViewModel()
) {
    val uploadState by viewModel.uploadState.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        Log.d(TAG, "Image selected: $uri")
        viewModel.setSelectedImage(uri)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yeni Əsər Əlavə Et") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Image selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp)
                    .clickable { imagePickerLauncher.launch("image/*") }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.PhotoCamera,
                                contentDescription = "Select Image",
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Şəkil seçmək üçün toxunun")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Başlıq") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Təsvir") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Qiymət (₺)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Kateqoriya") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    Log.d(TAG, "Save button clicked, creating artwork")
                    val artwork = Artwork(
                        title = title,
                        description = description,
                        price = price.toDoubleOrNull() ?: 0.0,
                        category = category
                    )
                    viewModel.addArtwork(artwork)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotEmpty() && price.isNotEmpty() && !uploadState.isLoading
            ) {
                Text("Yadda Saxla")
            }

            // Upload progress indicator
            if (uploadState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Eser yükleniyor...",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Error message
            if (uploadState.error.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uploadState.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Success action
            LaunchedEffect(uploadState.isSuccess) {
                if (uploadState.isSuccess) {
                    Log.d(TAG, "Upload successful, navigating back")
                    viewModel.resetUploadState()
                    onNavigateBack()
                }
            }
        }
    }
}