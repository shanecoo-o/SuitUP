package com.suitup.app.data.repository.remote

import com.suitup.app.data.mapper.toDomain
import com.suitup.app.data.remote.catalog.CatalogApi
import com.suitup.app.data.remote.catalog.CreateSuitModelRequestDto
import com.suitup.app.data.remote.catalog.UpdateSuitModelRequestDto
import com.suitup.app.data.remote.http.ApiResult
import com.suitup.app.data.remote.http.map
import com.suitup.app.domain.model.SuitModel

class RemoteCatalogRepository(private val api: CatalogApi) {
    suspend fun getActiveModels(): ApiResult<List<SuitModel>> =
        api.getSuitModels().map { models -> models.map { it.toDomain() } }

    suspend fun getModel(id: String): ApiResult<SuitModel> =
        api.getSuitModelById(id).map { it.toDomain() }

    suspend fun getAllForAdmin(): ApiResult<List<SuitModel>> =
        api.adminGetAllSuitModels().map { models -> models.map { it.toDomain() } }

    suspend fun create(request: CreateSuitModelRequestDto): ApiResult<SuitModel> =
        api.adminCreateSuitModel(request).map { it.toDomain() }

    suspend fun update(id: String, request: UpdateSuitModelRequestDto): ApiResult<SuitModel> =
        api.adminUpdateSuitModel(id, request).map { it.toDomain() }

    suspend fun activate(id: String): ApiResult<SuitModel> =
        api.adminActivateSuitModel(id).map { it.toDomain() }

    suspend fun deactivate(id: String): ApiResult<SuitModel> =
        api.adminDeactivateSuitModel(id).map { it.toDomain() }
}
