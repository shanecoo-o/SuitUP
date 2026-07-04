package mz.ac.unizambeze.suitup.domain.model

/**
 * Representa as opções de estilo e parâmetros do fato (suit) selecionados pelo utilizador.
 * De acordo com a arquitetura "Intelligent Client" (Cliente Inteligente), as regras de preço
 * e validações de medidas são cacheadas e geridas também do lado do cliente.
 */
data class SuitConfig(
    val id: String,
    val model: String,       // Ex: "Classic Slim-Fit", "Modern Double-Breasted", "Zambeze Imperial"
    val lapel: String,       // Ex: "Notched Lapel" (Lapela Entalhada), "Peak Lapel", "Shawl Lapel"
    val fabricColor: String, // Hex ou nome da cor (Ex: Navy Blue, Imperial Charcoal, Gold Accented)
    val liningPattern: String, // Tipo de forro interior (Ex: Solid Maroon, Royal Paisley, Vibrant Silk)
    val buttonType: String,  // Ex: "Single Button", "Double Breasted (2x2)"
    val pocketStyle: String, // Ex: "Flap Pockets", "Jetted Pockets"
    val fitType: String      // Ex: "Slim Fit", "Modern Fit", "Classic Fit"
)
