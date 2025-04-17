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

@HiltViewModel
class ArtBuyerViewModel @Inject constructor(
    private val repository: ArtRepository
) : ViewModel() {

    private val _artworksState = MutableStateFlow<ArtworksState>(ArtworksState())
    val artworksState = _artworksState.asStateFlow()

    init {
        getAllArtworks()
    }

    fun getAllArtworks() {
        viewModelScope.launch {
            repository.getAllArtworks().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _artworksState.value = ArtworksState(
                            artworks = result.data ?: emptyList()
                        )
                    }
                    is Resource.Error -> {
                        _artworksState.value = ArtworksState(
                            error = result.message ?: "Bilinməyən xəta"
                        )
                    }
                    is Resource.Loading -> {
                        _artworksState.value = ArtworksState(isLoading = true)
                    }
                }
            }
        }
    }

    // State sinifi
    data class ArtworksState(
        val isLoading: Boolean = false,
        val artworks: List<Artwork> = emptyList(),
        val error: String = ""
    )
}