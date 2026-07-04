package com.suitup.app.data.remote.auth

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
)

interface TokenStore {
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun clearTokens()
}

class InMemoryTokenStore : TokenStore {
    private val mutex = Mutex()
    private var tokens: TokenPair? = null

    override suspend fun getAccessToken(): String? = mutex.withLock { tokens?.accessToken }

    override suspend fun getRefreshToken(): String? = mutex.withLock { tokens?.refreshToken }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        require(accessToken.isNotBlank()) { "O access token não pode estar vazio" }
        require(refreshToken.isNotBlank()) { "O refresh token não pode estar vazio" }
        mutex.withLock { tokens = TokenPair(accessToken, refreshToken) }
    }

    override suspend fun clearTokens() {
        mutex.withLock { tokens = null }
    }
}
