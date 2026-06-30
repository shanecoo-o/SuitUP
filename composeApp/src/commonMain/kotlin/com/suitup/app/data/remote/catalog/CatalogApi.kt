package com.suitup.app.data.remote.catalog

import com.suitup.app.data.remote.config.ApiConfig
import com.suitup.app.data.remote.http.ApiResult
import com.suitup.app.data.remote.http.RemoteJson
import com.suitup.app.data.remote.http.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import kotlinx.serialization.json.Json

class CatalogApi(
    private val client: HttpClient,
    private val config: ApiConfig,
    private val json: Json = RemoteJson.instance,
) {
    suspend fun getSuitModels(): ApiResult<List<SuitModelDto>> =
        safeApiCall(json) { client.get(config.url("/api/suit-models")) }

    suspend fun getSuitModelById(id: String): ApiResult<SuitModelDto> =
        safeApiCall(json) { client.get(config.url("/api/suit-models/$id")) }

    suspend fun adminGetAllSuitModels(): ApiResult<List<SuitModelDto>> =
        safeApiCall(json) { client.get(config.url("/api/admin/suit-models")) }

    suspend fun adminGetSuitModelById(id: String): ApiResult<SuitModelDto> =
        safeApiCall(json) { client.get(config.url("/api/admin/suit-models/$id")) }

    suspend fun adminCreateSuitModel(
        request: CreateSuitModelRequestDto,
    ): ApiResult<SuitModelDto> = safeApiCall(json) {
        client.post(config.url("/api/admin/suit-models")) { setBody(request) }
    }

    suspend fun adminUpdateSuitModel(
        id: String,
        request: UpdateSuitModelRequestDto,
    ): ApiResult<SuitModelDto> = safeApiCall(json) {
        client.put(config.url("/api/admin/suit-models/$id")) { setBody(request) }
    }

    suspend fun adminActivateSuitModel(id: String): ApiResult<SuitModelDto> =
        safeApiCall(json) { client.patch(config.url("/api/admin/suit-models/$id/activate")) }

    suspend fun adminDeactivateSuitModel(id: String): ApiResult<SuitModelDto> =
        safeApiCall(json) { client.patch(config.url("/api/admin/suit-models/$id/deactivate")) }
}
