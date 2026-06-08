package com.suitup.app.ui.util

import androidx.compose.ui.graphics.Color

/**
 * Converte "#1F2A44" ou "1F2A44" em Color do Compose.
 * Aceita 6 hex (RGB) ou 8 hex (ARGB). Retorna null se inválido.
 */
fun String.toComposeColorOrNull(): Color? {
    return runCatching {
        val cleaned = removePrefix("#")
        val withAlpha = when (cleaned.length) {
            6 -> "FF$cleaned"
            8 -> cleaned
            else -> return@runCatching null
        }
        Color(withAlpha.toLong(16).toInt())
    }.getOrNull()
}
