package com.suitup.app.data.remote.upload

import com.suitup.app.data.remote.config.ApiConfig
import com.suitup.app.data.remote.http.ApiResult
import com.suitup.app.data.remote.http.RemoteJson
import com.suitup.app.data.remote.http.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json

class FileUploadApi(
    private val client: HttpClient,
    private val config: ApiConfig,
    private val json: Json = RemoteJson.instance,
) {
    suspend fun uploadFile(
        payload: UploadFilePayload,
        purpose: UploadedFilePurposeDto,
    ): ApiResult<StoredFileDto> = safeApiCall(json) {
        client.submitFormWithBinaryData(
            url = config.url("/api/files/upload"),
            formData = formData {
                append("purpose", purpose.name)
                appendFile("file", payload)
            },
        )
    }

    suspend fun uploadPaymentProof(
        orderId: String,
        payload: UploadFilePayload,
    ): ApiResult<StoredFileDto> = safeApiCall(json) {
        client.submitFormWithBinaryData(
            url = config.url("/api/orders/$orderId/payment/proof"),
            formData = formData { appendFile("file", payload) },
        )
    }

    suspend fun uploadSuitModelImage(
        suitModelId: String,
        payload: UploadFilePayload,
    ): ApiResult<StoredFileDto> = safeApiCall(json) {
        client.submitFormWithBinaryData(
            url = config.url("/api/admin/suit-models/$suitModelId/image"),
            formData = formData { appendFile("file", payload) },
        )
    }

    suspend fun downloadFile(fileId: String): ApiResult<DownloadedFile> = safeApiCall(
        json = json,
        request = { client.get(config.url("/api/files/$fileId")) },
    ) { response ->
        DownloadedFile(
            fileId = fileId,
            bytes = response.body<ByteArray>(),
            contentType = response.headers[HttpHeaders.ContentType],
            contentDisposition = response.headers[HttpHeaders.ContentDisposition],
        )
    }

    private fun io.ktor.client.request.forms.FormBuilder.appendFile(
        fieldName: String,
        payload: UploadFilePayload,
    ) {
        val safeFilename = payload.filename.replace("\"", "_")
        append(
            key = fieldName,
            value = payload.bytes,
            headers = Headers.build {
                append(HttpHeaders.ContentType, payload.contentType)
                append(HttpHeaders.ContentDisposition, "filename=\"$safeFilename\"")
            },
        )
    }
}
