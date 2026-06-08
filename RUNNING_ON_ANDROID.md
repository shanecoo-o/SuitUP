# SuitUP · Como correr e fazer debug num Android físico

Guia detalhado: do código que tens em mãos até **o app a correr no teu Android** com Voyager Navigator (Step 2).

---

## 1. Pré-requisitos (instalar uma vez)

### 1.1 — Java JDK 17

```bash
java -version    # deve mostrar 17.x ou superior
```

Se não:
- **macOS:** `brew install openjdk@17`
- **Linux:** `sudo apt install openjdk-17-jdk`
- **Windows:** [Eclipse Temurin 17](https://adoptium.net/temurin/releases/?version=17)

### 1.2 — Android Studio

Descarrega do [developer.android.com/studio](https://developer.android.com/studio). Versão **Hedgehog (2023.1.1)** ou mais recente.

Aceita o SDK default. Vai descarregar:
- Android SDK Platform 34
- Android SDK Build-Tools
- Android Emulator
- Platform-Tools (inclui `adb`)

### 1.3 — Kotlin Multiplatform Mobile plugin

No Android Studio: **Settings/Preferences → Plugins** → procura "Kotlin Multiplatform" → instala e reinicia.

---

## 2. Preparar o telefone Android

### 2.1 — Ativar Developer Options

No telefone: **Definições → Sobre o telefone** → toca **7 vezes** em "Número de compilação".

### 2.2 — Ativar USB Debugging

**Definições → Sistema → Opções de programador** → ativa **"Depuração USB"**.

### 2.3 — Ligar o telefone

Liga ao computador com cabo USB **de dados**. No telefone aparece popup "Autorizar depuração USB?" → toca **OK**.

Verifica:
```bash
adb devices
```

Deves ver:
```
List of devices attached
abc123def456    device
```

Se "unauthorized" → aceita de novo o popup. Se nada aparece → troca cabo/porta.

---

## 3. Abrir o projeto no Android Studio

### 3.1 — Descompactar o ZIP

Extrai `SuitUp.zip` para uma pasta sem espaços ou acentos no caminho.

### 3.2 — Gerar Gradle Wrapper

Na raiz do projeto (`SuitUp/`):

```bash
gradle wrapper --gradle-version 8.10
```

Se não tens gradle:
- **macOS:** `brew install gradle`
- **Linux:** `sudo apt install gradle`
- **Windows:** Chocolatey ou download manual

### 3.3 — Abrir no Android Studio

1. **File → Open** → seleciona a pasta `SuitUp/`
2. Aguarda Gradle sync (5-15 min na primeira vez)
3. Quando aparecer "Gradle sync completed successfully" → estás pronto

**Se erro no sync:**
- "JDK location" → **File → Project Structure → SDK Location → Gradle Settings → Gradle JDK** → escolhe JDK 17
- "Android SDK" → **Settings → System Settings → Android SDK** → confirma o caminho

---

## 4. Correr no telefone

1. Topo do Android Studio, dropdown ao lado do botão Play (▶)
2. Seleciona o teu device (ex: "Samsung SM-G998B")
3. Clica **▶ Play** ou `Ctrl+R` (macOS) / `Shift+F10` (Windows/Linux)

Primeira build: 3-8 min. Depois é rápido.

**Apply Changes** (mantém state):
- **macOS:** `Cmd+Shift+Ctrl+R`
- **Windows/Linux:** `Ctrl+F10`

---

## 5. O que vais ver — comportamento esperado (Step 2)

⚠️ **Diferente do Step 1.** Já **não há** seletor de 16 ecrãs.

### Fluxo de abertura

1. **Splash** aparece (logo + tagline) → **auto-avança após 1.8s**
2. **Onboarding** com 3 páginas → swipe horizontal ou botão "Seguinte". Em qualquer momento podes tocar "Saltar"
3. **Login** com email/password → toca **"Entrar"** (não valida nada — demo)
4. **MainShell** abre na tab Início

### MainShell com bottom nav real

Vês 4 separadores no fundo: **Início · Modelos · Pedidos · Perfil**

- **Tocar num separador** muda de tab instantaneamente
- **Cada tab tem stack independente** — se entras em "Pedidos → toca num pedido → vês TrackOrder", depois mudas para "Início", e voltas a "Pedidos" → **continuas no TrackOrder**, não na lista

### Push-on-top: Cart e Checkout

- O ícone do **carrinho no canto superior direito** (em qualquer ecrã) abre o Cart **por cima** da tab atual — bottom nav desaparece
- No Cart, "Finalizar pedido" abre o **flow checkout de 4 passos** com step indicator no topo
- O botão **costas** físico do Android volta pela stack
- Na **Confirmação** (passo 4) o botão "Ver meus pedidos" salta para a tab Pedidos automaticamente, "Voltar ao início" salta para Home

### Logout

- Tab Perfil → "Sair" → app volta ao **Splash** e o fluxo recomeça

### Editor 2D e Preview 3D

- Tab Modelos → escolhe um modelo → entra no Editor 2D Partes → "Seguinte" → Editor 2D Cores → "Seguinte" → Preview 3D
- No Preview 3D: drag horizontal sobre o fato → roda. Botões "Girar", "Zoom", "Luz", "Fundo" alteram estado
- "Encomendar" → abre o flow checkout direto

---

## 6. Debug

### 6.1 — Logcat

Painel inferior: **Logcat**. Filtros úteis:
- `package:com.suitup.app` — só logs do app
- `level:error` — só erros

### 6.2 — Layout Inspector

**View → Tool Windows → Layout Inspector** → seleciona o processo `com.suitup.app`. Mostra a árvore de composables.

### 6.3 — Breakpoints

Clica na margem esquerda de qualquer linha → círculo vermelho. Usa botão **🐞 Debug**.

### 6.4 — Verificar a stack de navegação

Se algum ecrã abre num sítio inesperado, abre o Logcat com filtro:
```
tag:Voyager
```

Voyager loga as transições internas (push, pop, replace).

---

## 7. Sem cabo USB: debug por Wi-Fi (Android 11+)

Setup com USB primeiro:

1. **Opções de programador → Depuração sem fios** → Ativar
2. Toca **"Emparelhar dispositivo com código de emparelhamento"**
3. Aparece IP + porta + código
4. Terminal:

```bash
adb pair 192.168.x.x:PORTA
# digita o código
adb connect 192.168.x.x:OUTRA_PORTA
```

Desliga o USB.

---

## 8. Problemas comuns

| Sintoma | Solução |
|---|---|
| "Could not find Gradle wrapper" | `gradle wrapper --gradle-version 8.10` na raiz |
| "Module not specified" no Run config | **File → Sync Project with Gradle Files** |
| "Failed to apply plugin org.jetbrains.kotlin.android" | **File → Project Structure → SDK Location → Gradle JDK** → JDK 17 |
| App abre mas ecrã preto | Logcat com `level:error` |
| `Unresolved referencia: SuitTopBar` etc | **Build → Clean Project** + **Rebuild Project** |
| Build infinita | **File → Invalidate Caches and Restart** |
| Telefone não aparece em `adb devices` | Troca cabo/porta, reinstala drivers do fabricante |
| `IllegalStateException: Navigator not found` | Algum VoyagerScreen está a tentar aceder a LocalNavigator fora de scope. Avisa-me com a stack trace |
| App crasha após login | Provavelmente erro no MainShellScreen ou Tab. Logcat → procura stack trace com `com.suitup.app.ui.navigation` |

---

## 9. Próximo passo (Step 3)

Quando confirmares que o Step 2 funciona, o **Step 3** vai:

- Substituir `var x by remember { mutableStateOf(...) }` nos VoyagerScreens por `rememberScreenModel { XxxScreenModel() }`
- Cada ScreenModel terá `state: StateFlow<XxxUiState>` + `sealed class XxxUiEvent`
- Renomear `XxxVoyagerScreen` → `XxxScreen`, e o composable Step 1 → `XxxContent`

State sobrevive a config changes (rotação no Android) sem perder o conteúdo.

---

## 10. Tens problemas?

Copia a linha do erro do Logcat/Build output e responde-me. Os mais prováveis no Step 2:

1. **Versão do Voyager** — se o Gradle não resolve `cafe.adriel.voyager:voyager-navigator:1.1.0-beta02`, posso baixar para `1.0.0-rc10` (mais estável mas sem features novas)
2. **Compose Multiplatform 1.7 vs 1.6** — se o sync falhar, posso ajustar
3. **TabNavigator a perder estado quando trocas tab** — possível bug do Voyager beta. Se acontecer, há workaround com `key()` que adiciono
