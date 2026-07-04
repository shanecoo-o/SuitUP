package mz.ac.unizambeze.suitup.domain.repository

import kotlinx.coroutines.flow.Flow
import mz.ac.unizambeze.suitup.domain.model.Order
import mz.ac.unizambeze.suitup.domain.model.SuitConfig

/**
 * Interface do repositório compartilhado (Common Main) em Kotlin Multiplatform.
 * Define ações essenciais para salvar, recuperar, sincronizar dados locais com
 * a nuvem e lidar de forma resiliente com ambientes offline.
 */
interface SuitRepository {

    /**
     * Obtém o fluxo reativo de todos os pedidos salvos localmente
     */
    fun getLocalOrders(): Flow<List<Order>>

    /**
     * Cria um novo pedido localmente. 
     * Se o dispositivo estiver offline, salva como `syncPending = true`.
     * Se estiver online, envia imediatamente para o servidor Spring Boot.
     */
    suspend fun saveOrder(order: Order, isOffline: Boolean)

    /**
     * Sincroniza todos os pedidos criados offline ("Store & Forward").
     * Envia em lote para o endpoint Spring Boot `/api/orders/sync`.
     * Em caso de sucesso, atualiza o status `syncPending` local para falso.
     */
    suspend fun syncOfflineOrders(): Result<Unit>

    /**
     * Traz o estado atualizado do rastreamento (tracking) de um pedido guardado no servidor central.
     */
    suspend fun fetchOrderStatusFromServer(orderId: String): Result<Order>
}
