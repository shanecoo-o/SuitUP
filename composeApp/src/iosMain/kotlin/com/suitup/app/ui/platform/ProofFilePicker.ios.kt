package com.suitup.app.ui.platform

import androidx.compose.runtime.Composable

@Composable
actual fun rememberProofFilePicker(
    onSelected: (SelectedProofFile) -> Unit,
    onError: (String) -> Unit,
): () -> Unit = { onError("Selecção de comprovativo disponível actualmente no Android.") }
