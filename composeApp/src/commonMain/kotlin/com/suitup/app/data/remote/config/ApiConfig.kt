package com.suitup.app.data.remote.config

enum class ApiEnvironment(val defaultBaseUrl: String) {
    AndroidEmulator("http://10.0.2.2:8080"),
    PhysicalDevice("http://192.168.1.100:8080"),
    Desktop("http://localhost:8080"),
}

data class ApiConfig(
    val baseUrl: String = ApiEnvironment.AndroidEmulator.defaultBaseUrl,
    val enableLogging: Boolean = true,
    val requestTimeoutMillis: Long = 30_000,
    val connectTimeoutMillis: Long = 15_000,
    val socketTimeoutMillis: Long = 30_000,
) {
    init {
        require(baseUrl.startsWith("http://") || baseUrl.startsWith("https://")) {
            "A URL da API deve começar por http:// ou https://"
        }
    }

    fun url(path: String): String = "${baseUrl.trimEnd('/')}/${path.trimStart('/')}"

    companion object {
        fun androidEmulator(): ApiConfig = ApiConfig(ApiEnvironment.AndroidEmulator.defaultBaseUrl)

        fun desktop(): ApiConfig = ApiConfig(ApiEnvironment.Desktop.defaultBaseUrl)

        fun physicalDevice(host: String, port: Int = 8080): ApiConfig =
            ApiConfig(baseUrl = "http://${host.trim()}:$port")
    }
}
