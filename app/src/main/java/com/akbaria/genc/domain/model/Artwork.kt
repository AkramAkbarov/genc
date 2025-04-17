package com.akbaria.genc.domain.model

data class Artwork(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val imageUrl: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val createdAt: Long = 0
)