package com.suitup.app.data.catalog

import com.suitup.app.data.mock.MockCatalogStore
import com.suitup.app.data.remote.catalog.CreateSuitModelRequestDto
import com.suitup.app.data.remote.catalog.UpdateSuitModelRequestDto
import com.suitup.app.data.remote.http.ApiError
import com.suitup.app.data.remote.http.ApiResult
import com.suitup.app.data.repository.remote.RemoteCatalogRepository
import com.suitup.app.domain.model.SuitModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class AdminCatalogState(
    val models: List<SuitModel> = emptyList(),
    val isLoading: Boolean = false,
    val pendingModelId: String? = null,
    val source: CatalogDataSource? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val sessionExpired: Boolean = false,
) {
    val isUsingMockFallback: Boolean
        get() = source == CatalogDataSource.MOCK && errorMessage != null
}

sealed interface AdminCatalogOperationResult {
    data class Success(val model: SuitModel) : AdminCatalogOperationResult
    data class Failure(
        val message: String,
        val sessionExpired: Boolean = false,
    ) : AdminCatalogOperationResult
}

class AdminCatalogRepository(
    private val remoteRepository: RemoteCatalogRepository,
    private val customerRepository: CustomerCatalogRepository,
    private val mode: CatalogDataSourceMode = CatalogDataSourceConfig.mode,
) {
    private val operationMutex = Mutex()
    private val _state = MutableStateFlow(AdminCatalogState())
    val state: StateFlow<AdminCatalogState> = _state.asStateFlow()

    suspend fun refresh() {
        operationMutex.withLock {
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null,
                sessionExpired = false,
            )
            _state.value = when (mode) {
                CatalogDataSourceMode.MOCK -> mockState()
                CatalogDataSourceMode.API,
                CatalogDataSourceMode.API_WITH_MOCK_FALLBACK -> loadFromApi()
            }
        }
    }

    suspend fun create(model: SuitModel): AdminCatalogOperationResult = operationMutex.withLock {
        if (mode == CatalogDataSourceMode.MOCK) {
            val local = model.copy(id = model.id.ifBlank { nextMockId() })
            MockCatalogStore.addSuitModel(local)
            applySuccess(local, "Modelo criado com sucesso.")
        } else {
            executeMutation("Modelo criado com sucesso.") {
                remoteRepository.create(model.toCreateRequest())
            }
        }
    }

    suspend fun update(model: SuitModel): AdminCatalogOperationResult = operationMutex.withLock {
        if (mode == CatalogDataSourceMode.MOCK) {
            MockCatalogStore.updateSuitModel(model)
            applySuccess(model, "Modelo actualizado com sucesso.")
        } else {
            executeMutation("Modelo actualizado com sucesso.") {
                remoteRepository.update(model.id, model.toUpdateRequest())
            }
        }
    }

    suspend fun setAvailability(id: String, active: Boolean): AdminCatalogOperationResult =
        operationMutex.withLock {
            _state.value = _state.value.copy(
                pendingModelId = id,
                errorMessage = null,
                successMessage = null,
            )
            if (mode == CatalogDataSourceMode.MOCK) {
                if (active) MockCatalogStore.reactivateSuitModel(id) else MockCatalogStore.deactivateSuitModel(id)
                val updated = requireNotNull(MockCatalogStore.getSuitModelById(id))
                applySuccess(updated, if (active) "Modelo activado." else "Modelo desactivado.")
            } else {
                val result = if (active) remoteRepository.activate(id) else remoteRepository.deactivate(id)
                when (result) {
                    is ApiResult.Success -> applySuccess(
                        result.value,
                        if (active) "Modelo activado." else "Modelo desactivado.",
                    )
                    is ApiResult.Failure -> applyFailure(
                        result.error,
                        fallbackMessage = "Não foi possível alterar o estado do modelo.",
                    )
                }
            }
        }

    fun consumeSessionExpired() {
        _state.value = _state.value.copy(sessionExpired = false)
    }

    fun clearNotice() {
        _state.value = _state.value.copy(errorMessage = null, successMessage = null)
    }

    private suspend fun loadFromApi(): AdminCatalogState = when (val result = remoteRepository.getAllForAdmin()) {
        is ApiResult.Success -> {
            customerRepository.acceptAdminSnapshot(result.value)
            AdminCatalogState(models = result.value, source = CatalogDataSource.API)
        }
        is ApiResult.Failure -> when (result.error) {
            is ApiError.Unauthorized -> AdminCatalogState(
                errorMessage = "Sessão expirada. Faça login novamente.",
                sessionExpired = true,
            )
            is ApiError.Forbidden -> AdminCatalogState(errorMessage = "Sem permissão para gerir catálogo.")
            else -> if (mode == CatalogDataSourceMode.API_WITH_MOCK_FALLBACK) {
                mockState(errorMessage = "Não foi possível carregar os modelos.")
            } else {
                AdminCatalogState(errorMessage = "Não foi possível carregar os modelos.")
            }
        }
    }

    private suspend fun executeMutation(
        successMessage: String,
        request: suspend () -> ApiResult<SuitModel>,
    ): AdminCatalogOperationResult {
        _state.value = _state.value.copy(errorMessage = null, successMessage = null)
        return when (val result = request()) {
            is ApiResult.Success -> applySuccess(result.value, successMessage)
            is ApiResult.Failure -> applyFailure(result.error)
        }
    }

    private fun applySuccess(model: SuitModel, message: String): AdminCatalogOperationResult.Success {
        val updatedModels = _state.value.models
            .filterNot { it.id == model.id }
            .plus(model)
        _state.value = _state.value.copy(
            models = updatedModels,
            pendingModelId = null,
            source = if (mode == CatalogDataSourceMode.MOCK) CatalogDataSource.MOCK else CatalogDataSource.API,
            errorMessage = null,
            successMessage = message,
            sessionExpired = false,
        )
        if (mode != CatalogDataSourceMode.MOCK) {
            customerRepository.acceptAdminSnapshot(updatedModels)
        }
        return AdminCatalogOperationResult.Success(model)
    }

    private fun applyFailure(
        error: ApiError,
        fallbackMessage: String? = null,
    ): AdminCatalogOperationResult.Failure {
        val sessionExpired = error is ApiError.Unauthorized
        val message = when (error) {
            is ApiError.Unauthorized -> "Sessão expirada. Faça login novamente."
            is ApiError.Forbidden -> "Sem permissão para gerir catálogo."
            is ApiError.ValidationError -> "Dados inválidos. Verifique os campos."
            is ApiError.NotFound -> "Modelo não encontrado."
            is ApiError.Conflict -> "Já existe um modelo com estes dados."
            is ApiError.NetworkUnavailable -> fallbackMessage ?: "Não foi possível ligar ao servidor."
            is ApiError.ServerError,
            is ApiError.Unknown -> fallbackMessage ?: "Erro inesperado. Tente novamente."
        }
        _state.value = _state.value.copy(
            pendingModelId = null,
            errorMessage = message,
            successMessage = null,
            sessionExpired = sessionExpired,
        )
        return AdminCatalogOperationResult.Failure(message, sessionExpired)
    }

    private fun mockState(errorMessage: String? = null): AdminCatalogState = AdminCatalogState(
        models = MockCatalogStore.getAllSuitModels(),
        source = CatalogDataSource.MOCK,
        errorMessage = errorMessage,
    )

    private fun nextMockId(): String = "admin-${MockCatalogStore.getAllSuitModels().size + 1}"
}

private fun SuitModel.toCreateRequest(): CreateSuitModelRequestDto = CreateSuitModelRequestDto(
    name = name,
    category = category,
    description = description,
    price = basePrice.toDouble(),
    currency = currency,
    fabricType = fabricType,
    color = color,
    imageKey = imageKey,
    primaryImageFileId = primaryImageFileId,
    active = available,
)

private fun SuitModel.toUpdateRequest(): UpdateSuitModelRequestDto = UpdateSuitModelRequestDto(
    name = name,
    category = category,
    description = description,
    price = basePrice.toDouble(),
    currency = currency,
    fabricType = fabricType,
    color = color,
    imageKey = imageKey,
    primaryImageFileId = primaryImageFileId,
    active = available,
)
