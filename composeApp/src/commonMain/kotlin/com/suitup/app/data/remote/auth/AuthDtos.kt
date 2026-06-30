package com.suitup.app.data.remote.auth

import kotlinx.serialization.Serializable

@Serializable
enum class UserRoleDto { CUSTOMER, ADMIN }

@Serializable
data class RegisterRequestDto(
    val fullName: String,
    val email: String,
    val phone: String? = null,
    val password: String,
)

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String,
)

@Serializable
data class RefreshRequestDto(val refreshToken: String)

@Serializable
data class UserDto(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String? = null,
    val enabled: Boolean,
    val roles: Set<UserRoleDto> = emptySet(),
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresInSeconds: Long,
    val user: UserDto,
)
