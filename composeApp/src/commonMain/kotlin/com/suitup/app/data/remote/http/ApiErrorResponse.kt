package com.suitup.app.data.remote.http

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    val timestamp: String? = null,
    val status: Int? = null,
    val error: String? = null,
    val message: String? = null,
    val path: String? = null,
    val fieldErrors: Map<String, String> = emptyMap(),
)
