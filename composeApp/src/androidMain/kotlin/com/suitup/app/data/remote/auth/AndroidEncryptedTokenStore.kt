package com.suitup.app.data.remote.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class AndroidEncryptedTokenStore(context: Context) : TokenStore {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )
    private val mutex = Mutex()

    override suspend fun getAccessToken(): String? = readToken(ACCESS_TOKEN_KEY)

    override suspend fun getRefreshToken(): String? = readToken(REFRESH_TOKEN_KEY)

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        require(accessToken.isNotBlank()) { "O access token não pode estar vazio" }
        require(refreshToken.isNotBlank()) { "O refresh token não pode estar vazio" }

        withContext(Dispatchers.IO) {
            mutex.withLock {
                val encryptedAccessToken = encrypt(ACCESS_TOKEN_KEY, accessToken)
                val encryptedRefreshToken = encrypt(REFRESH_TOKEN_KEY, refreshToken)
                check(
                    preferences.edit()
                        .putString(ACCESS_TOKEN_KEY, encryptedAccessToken)
                        .putString(REFRESH_TOKEN_KEY, encryptedRefreshToken)
                        .commit()
                ) { "Não foi possível guardar a sessão de forma segura" }
            }
        }
    }

    override suspend fun clearTokens() {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                check(
                    preferences.edit()
                        .remove(ACCESS_TOKEN_KEY)
                        .remove(REFRESH_TOKEN_KEY)
                        .commit()
                ) { "Não foi possível terminar a sessão de forma segura" }
            }
        }
    }

    private suspend fun readToken(key: String): String? = withContext(Dispatchers.IO) {
        mutex.withLock {
            val encrypted = preferences.getString(key, null) ?: return@withLock null
            runCatching { decrypt(key, encrypted) }
                .getOrElse {
                    preferences.edit().clear().commit()
                    null
                }
        }
    }

    private fun encrypt(preferenceKey: String, plainText: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        cipher.updateAAD(preferenceKey.toByteArray(Charsets.UTF_8))
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val iv = Base64.encodeToString(cipher.iv, Base64.NO_WRAP)
        val payload = Base64.encodeToString(encrypted, Base64.NO_WRAP)
        return "$FORMAT_VERSION:$iv:$payload"
    }

    private fun decrypt(preferenceKey: String, storedValue: String): String {
        val parts = storedValue.split(':', limit = 3)
        require(parts.size == 3 && parts[0] == FORMAT_VERSION) { "Formato de sessão inválido" }

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = Base64.decode(parts[1], Base64.NO_WRAP)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))
        cipher.updateAAD(preferenceKey.toByteArray(Charsets.UTF_8))
        val plainText = cipher.doFinal(Base64.decode(parts[2], Base64.NO_WRAP))
        return plainText.toString(Charsets.UTF_8)
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }

        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE).run {
            init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(KEY_SIZE_BITS)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
            generateKey()
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "suitup_secure_session"
        const val ACCESS_TOKEN_KEY = "access_token"
        const val REFRESH_TOKEN_KEY = "refresh_token"
        const val KEY_ALIAS = "suitup_auth_token_key_v1"
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val FORMAT_VERSION = "v1"
        const val KEY_SIZE_BITS = 256
        const val GCM_TAG_LENGTH_BITS = 128
    }
}
