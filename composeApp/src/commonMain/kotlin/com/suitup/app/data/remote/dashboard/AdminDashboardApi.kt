package com.suitup.app.data.remote.dashboard

import com.suitup.app.data.remote.config.ApiConfig
import com.suitup.app.data.remote.http.ApiResult
import com.suitup.app.data.remote.http.RemoteJson
import com.suitup.app.data.remote.http.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.serialization.json.Json

class AdminDashboardApi(
    private val client: HttpClient,
    private val config: ApiConfig,
    private val json: Json = RemoteJson.instance,
) {
    suspend fun getDashboard(): ApiResult<AdminDashboardDto> =
        safeApiCall(json) { client.get(config.url("/api/admin/dashboard")) }
}
