package mz.ac.unizambeze.suitup.domain.model

/**
 * Representa as medidas corporais do cliente.
 * São utilizadas para efetuar o corte sob medida na alfaiataria.
 */
data class Measurements(
    val shoulders: Double, // Medida do ombro (em cm)
    val chest: Double,     // Medida do peito (em cm)
    val sleeves: Double,   // Medida da manga (em cm)
    val waist: Double      // Medida da cintura (em cm)
)

/**
 * Representa um pedido em si. Contém o ID público da encomenda,
 * a configuração selecionada, as medidas coletadas, o comprovativo M-Pesa
 * (representado pelo ID de transação ou caminho do ficheiro local) e o estado.
 */
data class Order(
    val id: String,
    val configuration: SuitConfig,
    val measurements: Measurements,
    val mpesaTransactionId: String,  // ID de transação para verificação manual no back-office
    val status: String,              // Ex: "Design Concluído", "Corte", "Confecção", "Finalizado"
    val timestamp: Long,             // Hora de criação
    val syncPending: Boolean = false // Se o registo foi criado offline e precisa de sincronização (Store-and-Forward)
)
