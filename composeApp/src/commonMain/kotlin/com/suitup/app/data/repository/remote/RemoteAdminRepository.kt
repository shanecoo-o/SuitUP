package com.suitup.app.data.repository.remote

import com.suitup.app.data.mapper.toDomain
import com.suitup.app.data.remote.dashboard.AdminDashboardApi
import com.suitup.app.data.remote.http.ApiResult
import com.suitup.app.data.remote.http.map
import com.suitup.app.domain.model.AdminDashboardSummary

class RemoteAdminRepository(private val dashboardApi: AdminDashboardApi) {
    suspend fun getDashboard(): ApiResult<AdminDashboardSummary> =
        dashboardApi.getDashboard().map { it.toDomain() }
}
