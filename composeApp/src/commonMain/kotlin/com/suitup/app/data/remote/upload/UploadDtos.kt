package com.suitup.app.data.remote.upload

import kotlinx.serialization.Serializable

@Serializable
enum class UploadedFilePurposeDto { SUIT_IMAGE, PAYMENT_PROOF, PROFILE, OTHER }

data class UploadFilePayload(
    val filename: String,
    val contentType: String,
    val bytes: ByteArray,
) {
    init {
        require(filename.isNotBlank()) { "O nome do ficheiro é obrigatório" }
        require('/' !in filename && '\\' !in filename) { "O nome não pode conter caminhos" }
        require(contentType.isNotBlank()) { "O content type é obrigatório" }
        require(bytes.isNotEmpty()) { "O ficheiro não pode estar vazio" }
    }
}

@Serializable
data class StoredFileDto(
    val fileId: String,
    val originalFilename: String,
    val contentType: String,
    val sizeBytes: Long,
    val purpose: UploadedFilePurposeDto,
    val createdAt: String,
    val url: String,
)

@Serializable
data class UploadedFileMetadataDto(
    val id: String,
    val ownerUserId: String? = null,
    val purpose: UploadedFilePurposeDto,
    val originalName: String,
    val storedName: String,
    val contentType: String,
    val sizeBytes: Long,
    val storagePath: String,
    val publicUrl: String? = null,
    val createdAt: String,
)

data class DownloadedFile(
    val fileId: String,
    val bytes: ByteArray,
    val contentType: String?,
    val contentDisposition: String?,
)
