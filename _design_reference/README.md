# SuitUP Design Reference Clean Package

Este pacote contém apenas material de referência visual para a branch `ui/figma-refactor`.

## Regra principal

Não substituir o projecto KMP actual por este material. Usar apenas para comparar layout, cores, componentes, spacing, tipografia e hierarquia visual.

## Base oficial da app

A app oficial continua em:

```text
composeApp/src/commonMain/kotlin/com/suitup/app/
```

Arquitectura oficial:

```text
Kotlin Multiplatform
Compose Multiplatform
Voyager
ScreenModels
StateFlow
```

## Conteúdo

```text
_design_reference/
├── android_compose_reference/
│   ├── components/      # componentes Compose Android para referência visual
│   ├── screens/         # screens Compose Android para referência visual
│   ├── theme/           # tokens visuais para comparar com SuitTheme
│   ├── data/            # MockData de referência
│   ├── model/           # Models de referência
│   └── navigation_reference_do_not_copy/ # navegação Android, não migrar directamente
├── figma_html_exports/  # HTML/CSS exportado por screen
├── figma_screens/       # PNGs completos por screen
└── docs/                # docs originais úteis
```

## O que NÃO foi incluído

Foram removidos de propósito:

```text
.gradle/
build/
app/build/
gradle/
gradlew
gradlew.bat
settings.gradle.kts
build.gradle.kts
app/build.gradle.kts
AndroidManifest.xml
MainActivity.kt
local.properties
.idea/
```

## Aviso ao Codex

- Não copiar Gradle daqui.
- Não copiar AndroidManifest daqui.
- Não copiar MainActivity daqui.
- Não migrar Navigation Compose para a app principal.
- Não carregar imagens externas em runtime.
- Screenshots/HTML são referência visual; a UI deve ser recriada em Compose Multiplatform.
- Código útil deve ser adaptado para `commonMain`, mantendo Voyager e ScreenModels.

## Ordem recomendada para uso

1. Ler `figma_screens/` para entender visual final.
2. Ler `figma_html_exports/*/code.html` para extrair spacing, cores e estrutura.
3. Ler `android_compose_reference/theme/` para comparar tokens.
4. Ler `android_compose_reference/components/` para traduzir componentes.
5. Ler `android_compose_reference/screens/` para refactor screen por screen.

## Ficheiros incluídos

- `android_compose_reference/components/Buttons.kt`
- `android_compose_reference/components/Cards.kt`
- `android_compose_reference/components/Inputs.kt`
- `android_compose_reference/components/ProgressRail.kt`
- `android_compose_reference/data/MockData.kt`
- `android_compose_reference/model/Models.kt`
- `android_compose_reference/navigation_reference_do_not_copy/AppNavigation.kt`
- `android_compose_reference/screens/CatalogScreen.kt`
- `android_compose_reference/screens/EditorScreen.kt`
- `android_compose_reference/screens/HomeScreen.kt`
- `android_compose_reference/screens/LoginScreen.kt`
- `android_compose_reference/screens/RegistrationScreen.kt`
- `android_compose_reference/screens/TrackingScreen.kt`
- `android_compose_reference/theme/Color.kt`
- `android_compose_reference/theme/Shape.kt`
- `android_compose_reference/theme/Spacing.kt`
- `android_compose_reference/theme/Theme.kt`
- `android_compose_reference/theme/Type.kt`
- `docs/sartorial_excellence_DESIGN.md`
- `docs/source_README.md`
- `figma_html_exports/catalog_suitup/code.html`
- `figma_html_exports/catalog_suitup/screen.png`
- `figma_html_exports/checkout_suitup/code.html`
- `figma_html_exports/checkout_suitup/screen.png`
- `figma_html_exports/editor_2d_suitup_1/code.html`
- `figma_html_exports/editor_2d_suitup_1/screen.png`
- `figma_html_exports/editor_2d_suitup_2/code.html`
- `figma_html_exports/editor_2d_suitup_2/screen.png`
- `figma_html_exports/guide_suitup/code.html`
- `figma_html_exports/guide_suitup/screen.png`
- `figma_html_exports/home_suitup_1/code.html`
- `figma_html_exports/home_suitup_1/screen.png`
- `figma_html_exports/home_suitup_2/code.html`
- `figma_html_exports/home_suitup_2/screen.png`
- `figma_html_exports/login_suitup_1/code.html`
- `figma_html_exports/login_suitup_1/screen.png`
- `figma_html_exports/login_suitup_2/code.html`
- `figma_html_exports/login_suitup_2/screen.png`
- `figma_html_exports/payment_suitup/code.html`
- `figma_html_exports/payment_suitup/screen.png`
- `figma_html_exports/profile_suitup/code.html`
- `figma_html_exports/profile_suitup/screen.png`
- `figma_html_exports/registration_suitup/code.html`
- `figma_html_exports/registration_suitup/screen.png`
- `figma_html_exports/success_suitup/code.html`
- `figma_html_exports/success_suitup/screen.png`
- `figma_html_exports/tracking_suitup_1/code.html`
- `figma_html_exports/tracking_suitup_1/screen.png`
- `figma_html_exports/tracking_suitup_2/code.html`
- `figma_html_exports/tracking_suitup_2/screen.png`
- `figma_screens/catalog_suitup.png`
- `figma_screens/checkout_suitup.png`
- `figma_screens/editor_2d_suitup_1.png`
- `figma_screens/editor_2d_suitup_2.png`
- `figma_screens/guide_suitup.png`
- `figma_screens/home_suitup_1.png`
- `figma_screens/home_suitup_2.png`
- `figma_screens/login_suitup_1.png`
- `figma_screens/login_suitup_2.png`
- `figma_screens/payment_suitup.png`
- `figma_screens/profile_suitup.png`
- `figma_screens/registration_suitup.png`
- `figma_screens/success_suitup.png`
- `figma_screens/tracking_suitup_1.png`
- `figma_screens/tracking_suitup_2.png`
