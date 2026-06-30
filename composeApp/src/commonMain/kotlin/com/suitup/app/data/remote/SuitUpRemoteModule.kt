package com.suitup.app.data.remote

import com.suitup.app.data.remote.auth.AuthApi
import com.suitup.app.data.remote.auth.InMemoryTokenStore
import com.suitup.app.data.remote.auth.TokenStore
import com.suitup.app.data.remote.catalog.CatalogApi
import com.suitup.app.data.remote.config.ApiConfig
import com.suitup.app.data.remote.dashboard.AdminDashboardApi
import com.suitup.app.data.remote.http.HttpClientFactory
import com.suitup.app.data.remote.http.RemoteJson
import com.suitup.app.data.remote.orders.OrdersApi
import com.suitup.app.data.remote.payments.PaymentsApi
import com.suitup.app.data.remote.upload.FileUploadApi
import com.suitup.app.data.repository.remote.RemoteAdminRepository
import com.suitup.app.data.repository.remote.RemoteAuthRepository
import com.suitup.app.data.repository.remote.RemoteCatalogRepository
import com.suitup.app.data.repository.remote.RemoteFileRepository
import com.suitup.app.data.repository.remote.RemoteOrderRepository
import com.suitup.app.data.repository.remote.RemotePaymentRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider

class SuitUpRemoteModule(
    val config: ApiConfig = ApiConfig.androidEmulator(),
    val tokenStore: TokenStore = InMemoryTokenStore(),
) {
    val httpClient: HttpClient = HttpClientFactory.create(config, tokenStore, RemoteJson.instance)

    private val authApi = AuthApi(httpClient, config)
    private val catalogApi = CatalogApi(httpClient, config)
    private val ordersApi = OrdersApi(httpClient, config)
    private val paymentsApi = PaymentsApi(httpClient, config)
    private val dashboardApi = AdminDashboardApi(httpClient, config)
    private val fileUploadApi = FileUploadApi(httpClient, config)

    val authRepository = RemoteAuthRepository(authApi, tokenStore) {
        httpClient.authProvider<BearerAuthProvider>()?.clearToken()
    }
    val catalogRepository = RemoteCatalogRepository(catalogApi)
    val orderRepository = RemoteOrderRepository(ordersApi)
    val paymentRepository = RemotePaymentRepository(paymentsApi)
    val adminRepository = RemoteAdminRepository(dashboardApi)
    val fileRepository = RemoteFileRepository(fileUploadApi)

    fun close() {
        httpClient.close()
    }
}
