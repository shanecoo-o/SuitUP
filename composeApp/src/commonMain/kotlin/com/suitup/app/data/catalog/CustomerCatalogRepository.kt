package com.suitup.app.data.catalog

import com.suitup.app.data.mock.MockCatalogStore
import com.suitup.app.data.remote.http.ApiResult
import com.suitup.app.data.repository.remote.RemoteCatalogRepository
import com.suitup.app.domain.model.SuitModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

enum class CatalogDataSourceMode {
    MOCK,
    API,
    API_WITH_MOCK_FALLBACK,
}

enum class CatalogDataSource {
    API,
    MOCK,
}

object CatalogDataSourceConfig {
    val mode: CatalogDataSourceMode = CatalogDataSourceMode.API_WITH_MOCK_FALLBACK
}

data class CustomerCatalogState(
    val models: List<SuitModel> = emptyList(),
    val isLoading: Boolean = false,
    val source: CatalogDataSource? = null,
    val errorMessage: String? = null,
) {
    val isUsingMockFallback: Boolean
        get() = source == CatalogDataSource.MOCK && errorMessage != null
}

class CustomerCatalogRepository(
    private val remoteRepository: RemoteCatalogRepository,
    private val mode: CatalogDataSourceMode = CatalogDataSourceConfig.mode,
) {
    private val refreshMutex = Mutex()
    private val _state = MutableStateFlow(CustomerCatalogState())
    val state: StateFlow<CustomerCatalogState> = _state.asStateFlow()

    suspend fun refresh() {
        refreshMutex.withLock {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            _state.value = when (mode) {
                CatalogDataSourceMode.MOCK -> mockState()
                CatalogDataSourceMode.API -> loadFromApi(useMockFallback = false)
                CatalogDataSourceMode.API_WITH_MOCK_FALLBACK -> loadFromApi(useMockFallback = true)
            }
        }
    }

    fun acceptAdminSnapshot(allModels: List<SuitModel>) {
        _state.value = CustomerCatalogState(
            models = allModels.filter(SuitModel::available),
            source = CatalogDataSource.API,
        )
    }

    private suspend fun loadFromApi(useMockFallback: Boolean): CustomerCatalogState =
        when (val result = remoteRepository.getActiveModels()) {
            is ApiResult.Success -> CustomerCatalogState(
                models = result.value.filter(SuitModel::available),
                source = CatalogDataSource.API,
            )
            is ApiResult.Failure -> if (useMockFallback) {
                mockState(errorMessage = CATALOG_LOAD_ERROR)
            } else {
                CustomerCatalogState(errorMessage = CATALOG_LOAD_ERROR)
            }
        }

    private fun mockState(errorMessage: String? = null): CustomerCatalogState = CustomerCatalogState(
        models = MockCatalogStore.getActiveSuitModels(),
        source = CatalogDataSource.MOCK,
        errorMessage = errorMessage,
    )

    private companion object {
        const val CATALOG_LOAD_ERROR = "Não foi possível carregar o catálogo."
    }
}

object CatalogRuntime {
    private var customerRepository: CustomerCatalogRepository? = null
    private var adminCatalogRepository: AdminCatalogRepository? = null

    val repository: CustomerCatalogRepository
        get() = checkNotNull(customerRepository) {
            "CatalogRuntime deve ser inicializado antes de carregar o catálogo"
        }

    val adminRepository: AdminCatalogRepository
        get() = checkNotNull(adminCatalogRepository) {
            "CatalogRuntime deve ser inicializado antes de gerir o catálogo"
        }

    fun initialize(remoteRepository: RemoteCatalogRepository) {
        if (customerRepository == null) {
            val customer = CustomerCatalogRepository(remoteRepository)
            customerRepository = customer
            adminCatalogRepository = AdminCatalogRepository(remoteRepository, customer)
        }
    }
}
