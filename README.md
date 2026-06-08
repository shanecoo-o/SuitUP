# SuitUP

App moçambicana de personalização de fatos sob medida — Kotlin Multiplatform (Android · iOS · Desktop) com Compose Multiplatform 1.7.

Idioma: Português (PT-MZ). Moeda: Metical (MT).

## Estado

- [x] **Step 0**: setup KMP + design system completo
- [x] **Step 1**: 16 ecrãs com UI estática
- [x] **Step 2**: Voyager Navigator com navegação real
- [x] **Step 3**: ScreenModels + StateFlow + UiState/UiEvent nos ecrãs principais
- [ ] **Step 4**: Repository Pattern + backend Ktor/mock API
- [ ] **Step 5**: Offline-first sync
- [ ] **Step 6**: Editor 2D real com Canvas e layers
- [ ] **Step 7**: Preview 3D nativo com Filament + SceneKit

## Os 18 ecrãs

| # | Ecrã | Pasta | Tab |
|---|---|---|---|
| 01 | Splash | screens/auth | — (auth flow) |
| 02 | Onboarding | screens/auth | — |
| 03 | Login | screens/auth | — |
| 04 | Home | screens/home | Início |
| 05 | Selecionar Modelo | screens/catalog | Modelos |
| 06 | Editor 2D · Partes | screens/editor | (push) |
| 07 | Editor 2D · Cores | screens/editor | (push) |
| 08 | Preview 3D | screens/editor | (push) |
| 09 | Checkout · Dados do Cliente | screens/checkout | (push) |
| 10 | **Checkout · Medidas do Cliente** | screens/checkout | (push) |
| 11 | Tipo de Entrega | screens/checkout | (push) |
| 12 | Endereço / Pickup | screens/checkout | (push) |
| 13 | Pagamento M-Pesa | screens/checkout | (push) |
| 14 | Confirmação | screens/checkout | (push) |
| 15 | Acompanhar Pedido | screens/orders | (push) |
| 16 | Perfil | screens/profile | Perfil |
| 17 | Carrinho | screens/cart | (push) |
| 18 | Lista de Pedidos | screens/orders | Pedidos |

## Fluxo de checkout (5 passos)

```
Carrinho
  → Dados do Cliente        (step 1/5)
  → Medidas do Cliente      (step 2/5)
  → Tipo de Entrega         (step 3/5)
  → Endereço / Pickup       (step 3/5)
  → Pagamento M-Pesa        (step 4/5)
  → Confirmação             (step 5/5)
```

## Navegação

```
App (Voyager Navigator outer)
  └─ SplashVoyagerScreen
       ↓ replace
     OnboardingVoyagerScreen
       ↓ replace
     LoginVoyagerScreen  [ScreenModel: valida email + palavra-passe]
       ↓ replaceAll
     MainShellScreen
          └─ TabNavigator [Início · Modelos · Pedidos · Perfil]
               ├─ HomeTab → Home → push Cart/TrackOrder
               ├─ ModelsTab → SelectModel → push Editor → 3D → Cart
               ├─ OrdersTab → OrdersList → push TrackOrder
               └─ ProfileTab → Profile [logout → Splash via LocalSignOut]
```

## Como correr

```bash
# No Android Studio: File → Open → SuitUp/
# Aguardar Gradle sync
# Ligar dispositivo Android com USB Debugging
# Clicar Play (▶)
```

Ver [RUNNING_ON_ANDROID.md](RUNNING_ON_ANDROID.md) para guia detalhado.

## Convenções

- `XxxScreen.kt` — composable stateless (UI pura)
- `XxxScreenModel.kt` — lógica de apresentação com StateFlow
- `XxxVoyagerScreen` — destino navegável (liga Screen + ScreenModel)
- Domínio em português, termos técnicos da stack em inglês
