package com.akbaria.genc.domain.repository

import android.net.Uri
import com.akbaria.genc.domain.model.Artwork
import com.akbaria.genc.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ArtRepository {
    fun getSellerArtworks(): Flow<Resource<List<Artwork>>>
    fun addArtwork(artwork: Artwork): Flow<Resource<Artwork>>
    fun deleteArtwork(artworkId: String): Flow<Resource<Boolean>>
    fun uploadImage(imageUri: Uri): Flow<Resource<String>> // Şəkil yükləmək üçün əlavə edildi
}