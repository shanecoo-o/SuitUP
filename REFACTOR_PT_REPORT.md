# Relatório de Refatoração para Português — SuitUP

## Objectivo

Preparar o projecto SuitUP para a fase seguinte (**Step 3 — ScreenModels + StateFlow + UiState/UiEvent**) através de uma refatoração controlada dos nomes de domínio, variáveis principais e dados mockados para português.

## Estratégia usada

A refatoração foi feita de forma faseada e estratégica, evitando substituição cega de todos os termos em inglês.

Foram mantidos em inglês os nomes técnicos ligados a frameworks, bibliotecas e padrões comuns, como:

- `Screen`
- `Composable`
- `Navigator`
- `UiState`
- `UiEvent`
- `ScreenModel`
- `StateFlow`
- `Repository`
- nomes de componentes Compose/Voyager

## Principais classes renomeadas

| Antes | Depois |
|---|---|
| `User` | `Utilizador` |
| `Measurements` | `Medidas` |
| `SuitModel` | `ModeloFato` |
| `SuitCategory` | `CategoriaFato` |
| `SuitDesign` | `DesignFato` |
| `SuitParts` | `PartesFato` |
| `Fabric` | `Tecido` |
| `SuitColor` | `CorFato` |
| `Order` | `Pedido` |
| `DeliveryType` | `TipoEntrega` |
| `DeliveryAddress` | `EnderecoEntrega` |
| `PickupPoint` | `PontoLevantamento` |
| `OrderStatus` | `EstadoPedido` |
| `OrderEvent` | `EventoPedido` |
| `EventState` | `EstadoEvento` |
| `PaymentInfo` | `InfoPagamento` |
| `PaymentMethod` | `MetodoPagamento` |
| `CartItem` | `ItemCarrinho` |

## Principais propriedades/variáveis renomeadas

| Antes | Depois |
|---|---|
| `currentUser` | `utilizadorActual` |
| `suitModels` | `modelosFato` |
| `fabrics` | `tecidos` |
| `colors` | `coresFato` |
| `recentOrders` | `pedidosRecentes` |
| `sampleDesign` | `designExemplo` |
| `newOrder` | `novoPedido` |
| `cartItems` | `itensCarrinho` |
| `deliveryFeeMt` | `taxaEntregaMt` |
| `pickupPoints` | `pontosLevantamento` |
| `mozambicanCities` | `cidadesMocambicanas` |
| `maputoNeighborhoods` | `bairrosMaputo` |
| `mpesaNumber` | `numeroMpesa` |
| `mpesaTitle` | `titularMpesa` |

## Estados/enums traduzidos

| Antes | Depois |
|---|---|
| `AwaitingPayment` | `AguardandoPagamento` |
| `PaymentValidated` | `PagamentoValidado` |
| `InProduction` | `EmProducao` |
| `ReadyForDelivery` | `ProntoParaEntrega` |
| `Delivered` | `Entregue` |
| `Cancelled` | `Cancelado` |
| `Delivery` | `Entrega` |
| `Pickup` | `Levantamento` |
| `Done` | `Concluido` |
| `Current` | `Actual` |
| `Pending` | `Pendente` |

## Pastas preparadas para o Step 3/Step 4

Foram adicionadas pastas placeholder para preparar a arquitectura futura sem implementar lógica nova:

```text
core/validation/
domain/usecase/
domain/repository/
data/repository/
```

## O que NÃO foi implementado

Esta versão não implementa ainda:

- `ScreenModel`
- `StateFlow`
- `UiState`
- `UiEvent`
- Repository Pattern real
- backend Ktor
- autenticação real
- pagamento real
- editor 2D real
- preview 3D nativo

## Verificações feitas

- Modelos de domínio e `MockData` foram verificados com compilação isolada, removendo temporariamente a dependência de `kotlinx.serialization` para validar consistência de nomes e tipos.
- Foram pesquisadas referências antigas dos principais nomes em inglês para evitar imports quebrados.
- A estrutura de navegação foi mantida.
- Os 17 ecrãs foram preservados.

## Limitação da verificação

Não foi possível executar uma build Gradle completa no ambiente actual porque o projecto não inclui `gradlew` e o sistema não possui Gradle instalado. A validação final deve ser feita no Android Studio com:

```bash
gradle wrapper --gradle-version 8.10
./gradlew :composeApp:assembleDebug
```

No Windows:

```bash
gradlew.bat :composeApp:assembleDebug
```

## Veredicto

O projecto está pronto para ser aberto no Android Studio e validado. Depois da validação do build, pode avançar para o **Step 3 — ScreenModels + StateFlow + UiState/UiEvent**.
