package com.suitup.app.data.remote.catalog

import kotlinx.serialization.Serializable

@Serializable
data class SuitModelDto(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val price: Double,
    val currency: String,
    val fabricType: String,
    val color: String,
    val imageKey: String? = null,
    val primaryImageFileId: String? = null,
    val active: Boolean,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class CreateSuitModelRequestDto(
    val name: String,
    val category: String,
    val description: String,
    val price: Double,
    val currency: String = "MZN",
    val fabricType: String,
    val color: String,
    val imageKey: String? = null,
    val primaryImageFileId: String? = null,
    val active: Boolean = true,
)

@Serializable
data class UpdateSuitModelRequestDto(
    val name: String,
    val category: String,
    val description: String,
    val price: Double,
    val currency: String = "MZN",
    val fabricType: String,
    val color: String,
    val imageKey: String? = null,
    val primaryImageFileId: String? = null,
    val active: Boolean,
)
