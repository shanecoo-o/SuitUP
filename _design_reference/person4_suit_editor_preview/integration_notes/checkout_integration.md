# checkout_integration.md — Integração do Editor com Carrinho e Checkout

## Fluxo completo: Editor → Checkout

```
Editor (pessoa 4)
    ↓  buildFinalConfiguration()
FinalSuitConfiguration (JSON)
    ↓  adicionarAoCarrinho(config)
Carrinho (outra pessoa)
    ↓  confirmarCarrinho()
Medidas (outra pessoa)
    ↓  medidas preenchidas
Pagamento (outra pessoa)
    ↓  pagamento confirmado
Tracking / Produção
```

---

## Objecto que o editor entrega

```kotlin
data class FinalSuitConfiguration(
    val configurationId: String,            // UUID gerado no editor
    val modeloId: String,                   // "classic_suit"
    val cor: SuitColorOption,
    val tecido: FabricOption,
    val ajustes: Map<String, Float>,        // sliders: lapela, comprimento, ombro
    val detalhes: SuitDetails,              // lapela, botões, bolsos, fit
    val precoBase: Int,                     // em MZN (centavos)
    val precoTotal: Int,
    val previewLayers: List<EditorLayer>,   // layers activas no momento de confirmação
    val necessitaMedidas: Boolean = true
)

data class SuitDetails(
    val lapela: String = "notch",           // "notch" | "peak" | "shawl"
    val botoes: String = "two_buttons",     // "one_button" | "two_buttons" | "three_buttons"
    val bolsos: String = "flap",            // "flap" | "patch" | "welt"
    val fit: String = "slim",              // "slim" | "regular" | "relaxed"
    val forro: String = "standard"         // "standard" | "contrast" | "personalizado"
)
```

---

## Como o editor entrega ao carrinho

```kotlin
// No EditorViewModel
fun confirmAndAddToCart(cartViewModel: CartViewModel) {
    val config = buildFinalConfiguration()
    cartViewModel.addItem(
        CartItem(
            id          = config.configurationId,
            type        = CartItemType.SUIT,
            label       = "${config.cor.label} ${config.tecido.label} Suit",
            price       = config.precoTotal,
            configuration = config,
            requiresMeasurements = config.necessitaMedidas
        )
    )
    navigator.push(CartScreen())
}
```

---

## Como o checkout lê a configuração

```kotlin
// No CheckoutViewModel
fun processOrder(cartItems: List<CartItem>): Order {
    return Order(
        items = cartItems.map { item ->
            OrderLine(
                itemId        = item.id,
                description   = item.label,
                price         = item.price,
                configuration = item.configuration as? FinalSuitConfiguration,
                needsMeasures = item.requiresMeasurements
            )
        },
        totalPrice = cartItems.sumOf { it.price }
    )
}
```

---

## Campos obrigatórios que o editor deve preencher

| Campo | Tipo | Obrigatório | Notas |
|---|---|---|---|
| configurationId | String (UUID) | Sim | Gerado no editor |
| modeloId | String | Sim | Da lista em editor_config.json |
| cor.id | String | Sim | Da paleta de cores |
| cor.hex | String | Sim | Para exibir no carrinho |
| tecido.id | String | Sim | Do catálogo de tecidos |
| ajustes | Map<String,Float> | Sim | Todos os 3 sliders |
| precoBase | Int (MZN) | Sim | Calculado do modelo |
| precoTotal | Int (MZN) | Sim | Base + modificadores |
| necessitaMedidas | Boolean | Sim | Sempre true para fato |
| previewLayers | List | Recomendado | Para mostrar no checkout |

---

## Integração com o módulo de Medidas

```kotlin
// Após checkout confirmar pagamento:
MedidasViewModel.initForConfiguration(
    configId    = config.configurationId,
    suitModelId = config.modeloId
)
// Solicitar ao utilizador: peito, cintura, anca, comprimento manga, etc.
```

---

## Integração com Tracking / Produção

```kotlin
// Após medidas submetidas:
TrackingViewModel.createOrder(
    orderId     = order.id,
    configId    = config.configurationId,
    etapas      = listOf("Confirmado", "Em produção", "Controlo de qualidade", "Envio", "Entregue"),
    prazo       = "14 dias úteis"
)
```

---

## Exemplo de payload JSON enviado ao backend

Ver: `editor_data/final_suit_configuration_example.json`

O backend deve:
1. Validar o `configurationId` (UUID único)
2. Verificar disponibilidade do `tecido.id` e `cor.id`
3. Calcular preço final com IVA
4. Criar registo de encomenda
5. Reencaminhar para módulo de medidas
