package com.suitup.model

data class Suit(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String
)

data class Fabric(
    val id: String,
    val name: String,
    val type: String,
    val colorHex: String,
    val imageUrl: String
)

data class User(
    val id: String,
    val name: String,
    val email: String
)

data class Order(
    val id: String,
    val suitName: String,
    val status: String,
    val date: String,
    val total: Double
)
