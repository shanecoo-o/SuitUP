package com.suitup.app.data.repository.remote

import com.suitup.app.data.remote.http.ApiResult
import com.suitup.app.data.remote.upload.DownloadedFile
import com.suitup.app.data.remote.upload.FileUploadApi
import com.suitup.app.data.remote.upload.StoredFileDto
import com.suitup.app.data.remote.upload.UploadFilePayload
import com.suitup.app.data.remote.upload.UploadedFilePurposeDto

class RemoteFileRepository(private val api: FileUploadApi) {
    suspend fun upload(
        payload: UploadFilePayload,
        purpose: UploadedFilePurposeDto,
    ): ApiResult<StoredFileDto> = api.uploadFile(payload, purpose)

    suspend fun uploadPaymentProof(
        orderId: String,
        payload: UploadFilePayload,
    ): ApiResult<StoredFileDto> = api.uploadPaymentProof(orderId, payload)

    suspend fun uploadSuitModelImage(
        modelId: String,
        payload: UploadFilePayload,
    ): ApiResult<StoredFileDto> = api.uploadSuitModelImage(modelId, payload)

    suspend fun download(fileId: String): ApiResult<DownloadedFile> = api.downloadFile(fileId)
}
