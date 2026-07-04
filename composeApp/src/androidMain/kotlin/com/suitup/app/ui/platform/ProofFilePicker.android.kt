package com.suitup.app.ui.platform

import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
actual fun rememberProofFilePicker(
    onSelected: (SelectedProofFile) -> Unit,
    onError: (String) -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    val resolver = context.contentResolver
                    val contentType = resolver.getType(uri) ?: mimeFromName(uri.lastPathSegment.orEmpty())
                    val metadata = resolver.query(
                        uri,
                        arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
                        null,
                        null,
                        null,
                    )?.use { cursor ->
                        if (!cursor.moveToFirst()) null else {
                            val name = cursor.getString(0)
                            val size = if (cursor.isNull(1)) null else cursor.getLong(1)
                            name to size
                        }
                    }
                    if ((metadata?.second ?: 0L) > MAX_PROOF_BYTES) {
                        error("O comprovativo deve ter no máximo 10 MB.")
                    }
                    val filename = metadata?.first
                        ?.substringAfterLast('/')
                        ?.substringAfterLast('\\')
                        ?: "comprovativo"
                    val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
                        ?: error("Não foi possível ler o comprovativo.")
                    SelectedProofFile(filename, contentType, bytes)
                }
            }.onSuccess(onSelected).onFailure {
                onError(it.message ?: "Não foi possível abrir o comprovativo.")
            }
        }
    }
    return { launcher.launch(arrayOf("image/png", "image/jpeg", "application/pdf")) }
}

private fun mimeFromName(name: String): String = when (name.substringAfterLast('.', "").lowercase()) {
    "png" -> "image/png"
    "jpg", "jpeg" -> "image/jpeg"
    "pdf" -> "application/pdf"
    else -> "application/octet-stream"
}

private const val MAX_PROOF_BYTES = 10L * 1024L * 1024L
