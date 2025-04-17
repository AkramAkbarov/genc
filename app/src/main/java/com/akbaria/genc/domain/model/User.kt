package com.akbaria.genc.domain.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val bio: String = "",
    val profilePictureUrl: String = "",
    val isSeller: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)