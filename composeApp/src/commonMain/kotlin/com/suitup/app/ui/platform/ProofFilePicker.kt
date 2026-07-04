package com.suitup.app.ui.platform

import androidx.compose.runtime.Composable

data class SelectedProofFile(
    val filename: String,
    val contentType: String,
    val bytes: ByteArray,
)

@Composable
expect fun rememberProofFilePicker(
    onSelected: (SelectedProofFile) -> Unit,
    onError: (String) -> Unit,
): () -> Unit
