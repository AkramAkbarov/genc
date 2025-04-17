package com.akbaria.genc.peresantation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akbaria.genc.data.repository.ArtRepository
import com.akbaria.genc.domain.model.Artwork
import com.akbaria.genc.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.net.Uri

@HiltViewModel
class ArtSellerViewModel @Inject constructor(
    private val repository: ArtRepository
) : ViewModel() {

    private val _artworkState = MutableStateFlow(ArtworkState())
    val artworkState = _artworkState.asStateFlow()

    private val _uploadState = MutableStateFlow(UploadState())
    val uploadState = _uploadState.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri = _selectedImageUri.asStateFlow()

    init {
        getSellerArtworks()
    }

    fun setSelectedImage(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun getSellerArtworks() {
        viewModelScope.launch {
            _artworkState.value = _artworkState.value.copy(isLoading = true)
            repository.getSellerArtworks().collect { result ->
                _artworkState.value = when (result) {
                    is Resource.Success -> {
                        ArtworkState(artworks = result.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        ArtworkState(error = result.message ?: "Eserler yüklenemedi")
                    }
                    is Resource.Loading -> {
                        ArtworkState(isLoading = true)
                    }
                }
            }
        }
    }

    fun addArtwork(artwork: Artwork) {
        viewModelScope.launch {
            _uploadState.value = UploadState(isLoading = true)

            repository.addArtwork(artwork).collect { addResult ->
                when (addResult) {
                    is Resource.Success -> {
                        val artworkId = addResult.data ?: ""
                        val imageUri = _selectedImageUri.value

                        if (imageUri != null) {
                            repository.uploadImage(imageUri, artworkId).collect { uploadResult ->
                                _uploadState.value = when (uploadResult) {
                                    is Resource.Success -> {
                                        getSellerArtworks()
                                        UploadState(isSuccess = true)
                                    }
                                    is Resource.Error -> UploadState(
                                        error = uploadResult.message ?: "Resim yüklenemedi"
                                    )
                                    is Resource.Loading -> UploadState(isLoading = true)
                                }
                            }
                        } else {
                            _uploadState.value = UploadState(isSuccess = true)
                            getSellerArtworks()
                        }
                    }
                    is Resource.Error -> {
                        _uploadState.value = UploadState(
                            error = addResult.message ?: "Eser oluşturulamadı"
                        )
                    }
                    is Resource.Loading -> {
                        _uploadState.value = UploadState(isLoading = true)
                    }
                }
            }
        }
    }

    fun deleteArtwork(artworkId: String) {
        viewModelScope.launch {
            repository.deleteArtwork(artworkId).collect { result ->
                when (result) {
                    is Resource.Success -> getSellerArtworks()
                    is Resource.Error -> _artworkState.value = _artworkState.value.copy(
                        error = result.message ?: "Eser silinemedi"
                    )
                    is Resource.Loading -> _artworkState.value = _artworkState.value.copy(
                        isLoading = true
                    )
                }
            }
        }
    }

    fun resetUploadState() {
        _uploadState.value = UploadState()
    }

    data class ArtworkState(
        val isLoading: Boolean = false,
        val artworks: List<Artwork> = emptyList(),
        val error: String = ""
    )

    data class UploadState(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val error: String = ""
    )
}