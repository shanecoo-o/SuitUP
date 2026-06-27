package mz.ac.unizambeze.suitup.data.repository

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mz.ac.unizambeze.suitup.domain.model.Order
import mz.ac.unizambeze.suitup.domain.model.Measurements
import mz.ac.unizambeze.suitup.domain.model.SuitConfig
import mz.ac.unizambeze.suitup.domain.repository.SuitRepository

/**
 * IMPLEMENTAÇÃO REAL DO REPOSITÓRIO ENVOLVENTE (Kotlin Multiplatform).
 * Utiliza Ktor Client para comunicação HTTP com o backend Spring Boot
 * e simula a persistência local (que num projeto real interage com o driver SQLDelight SQLite).
 */
class SuitRepositoryImpl(
    private val httpClient: HttpClient,
    // Em KMP real, injetaríamos também o driver SQLDelight para fazer as queries SQL diretas:
    // private val database: SuitDatabaseQueries
) : SuitRepository {

    // Simulação em memória para ilustrar o armazenamento offline caso o SQLite real esteja instanciado
    private val cacheInMemory = mutableListOf<Order>()

    override fun getLocalOrders(): Flow<List<Order>> = flow {
        // Num fluxo real KMP, leríamos do banco SQLDelight:
        // emit(database.selectAllOrders().map { it.toDomain() })
        emit(cacheInMemory)
    }

    override suspend fun saveOrder(order: Order, isOffline: Boolean) {
        if (isOffline) {
            // "STORE" - Armazena localmente com flag de pendente
            val offlineOrder = order.copy(syncPending = true, status = "Design Concluído (Local)")
            cacheInMemory.add(offlineOrder)
            println("[KMP Cache] Guardado localmente para posterior sincronização: ${order.id}")
            
            // Num projeto real KMP executaria:
            // database.insertOrder(order.id, order.configuration.model, ..., syncPending = 1)
        } else {
            // Caso esteja online, envia imediatamente ao backend Java Spring Boot
            try {
                val response: HttpResponse = httpClient.post("https://api-suitup-be.unizambeze.ac.mz/api/orders") {
                    contentType(ContentType.Application.Json)
                    setBody(order)
                }

                if (response.status == HttpStatusCode.Created) {
                    val onlineOrder = order.copy(syncPending = false, status = "Enviado para Produção")
                    cacheInMemory.add(onlineOrder)
                } else {
                    // Fallback imediato se o servidor der erro (se comporta de forma offline)
                    cacheInMemory.add(order.copy(syncPending = true))
                }
            } catch (e: Exception) {
                // Fallback de resiliência (Sem Net)
                cacheInMemory.add(order.copy(syncPending = true))
            }
        }
    }

    override suspend fun syncOfflineOrders(): Result<Unit> {
        return try {
            // Filtrar itens na fila de Store-and-Forward
            val pendingOrders = cacheInMemory.filter { it.syncPending }
            if (pendingOrders.isEmpty()) return Result.success(Unit)

            // Envia todos os pendentes em lote para o servidor principal
            val response: HttpResponse = httpClient.post("https://api-suitup-be.unizambeze.ac.mz/api/orders/sync-batch") {
                contentType(ContentType.Application.Json)
                setBody(pendingOrders)
            }

            if (response.status == HttpStatusCode.OK) {
                // Sincronização completa. Limpar as flags locais
                val syncedList = cacheInMemory.map { 
                    if (it.syncPending) it.copy(syncPending = false, status = "Sincronizado & Recebido") else it
                }
                cacheInMemory.clear()
                cacheInMemory.addAll(syncedList)
                println("[KMP Sync] Sincronização em Lote concluída!")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Falha no servidor ao sincronizar: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchOrderStatusFromServer(orderId: String): Result<Order> {
        return try {
            // Chamada direta para o endpoint Spring Boot
            val response: HttpResponse = httpClient.get("https://api-suitup-be.unizambeze.ac.mz/api/orders/$orderId")
            if (response.status == HttpStatusCode.OK) {
                // Retorna o estado atualizado do banco de dados central do alfaiate
                val updatedOrder = response.body<Order>()
                Result.success(updatedOrder)
            } else {
                Result.failure(Exception("Erro ao buscar pedido: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
