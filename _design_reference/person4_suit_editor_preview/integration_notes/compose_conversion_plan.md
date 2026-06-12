# compose_conversion_plan.md — Plano de Conversão para Kotlin/Compose

## Visão geral

O protótipo web (`prototype_web/`) está em HTML/JS e serve exclusivamente como **referência de lógica**.
O código não deve ser copiado directamente para o projecto KMP.

Este documento mapeia cada módulo JS para o equivalente Compose/Kotlin.

---

## Mapa de conversão: JS → Kotlin/Compose

| Ficheiro JS | Equivalente Kotlin | Localização sugerida |
|---|---|---|
| `config.js` | `EditorConfig.kt` (object/companion) | `editor/EditorConfig.kt` |
| `state.js` | `EditorState.kt` + `EditorViewModel.kt` | `editor/EditorViewModel.kt` |
| `fase1_layer_manager.js` | `LayerManager.kt` + `SuitLayerStack.kt` | `editor/layers/` |
| `fase2_customization.js` | `ColorFilter.kt` + `BitmapShader.kt` + `SuitSlider.kt` | `editor/customization/` |
| `fase3_preview.js` | `AnglePreviewPager.kt` + `MannequinPreview.kt` | `editor/preview/` |
| `fase4_report.js` | `EditorReport.kt` + `ReportScreen.kt` | `editor/report/` |
| `app.js` | `EditorScreen.kt` (Voyager Screen) | `editor/EditorScreen.kt` |
| `index.html` | `EditorScreen.kt` layout | (integrado no Scaffold) |
| `styles.css` | `SuitEditorTheme.kt` (MaterialTheme) | `ui/theme/SuitEditorTheme.kt` |

---

## FASE 1 — Layer Manager → Compose

### JS (referência)
```javascript
// fase1_layer_manager.js
LayerManager.toggleVisibility('lapel')
LayerManager.validateComposition()
```

### Kotlin/Compose (implementação)
```kotlin
// LayerManager.kt
class LayerManagerViewModel : ScreenModel {
    private val _layers = MutableStateFlow(EditorLayerFactory.defaults())
    val layers: StateFlow<List<EditorLayer>> = _layers

    fun toggleVisibility(layerId: String) {
        _layers.update { list ->
            list.map { if (it.id == layerId) it.copy(visible = !it.visible) else it }
        }
    }
}

// SuitLayerStack.kt (Composable)
@Composable
fun SuitLayerStack(layers: List<EditorLayer>, angle: PreviewAngle, color: Color) {
    Box(Modifier.fillMaxSize()) {
        layers
            .filter { it.angle == angle && it.visible }
            .sortedBy { it.zOrder }
            .forEach { layer ->
                Image(
                    painter = painterResource(layer.assetResId),
                    colorFilter = if (layer.canChangeColor)
                        ColorFilter.tint(color, BlendMode.SrcIn) else null,
                    modifier = Modifier.fillMaxSize()
                )
            }
    }
}
```

---

## FASE 2 — Customização → Compose

### ColorFilter
```kotlin
// Aplicar cor com BlendModeColorFilter
val selectedColor by viewModel.state.collectAsState()
Image(
    painter = painterResource(R.drawable.layer_front_jacket_base),
    colorFilter = ColorFilter.tint(
        Color(android.graphics.Color.parseColor(selectedColor.hex)),
        BlendMode.SrcIn
    )
)
```

### BitmapShader (textura de tecido)
```kotlin
// Usando ShaderBrush em Canvas Compose
val textureBitmap = ImageBitmap.imageResource(R.drawable.fabric_wool)
val shaderBrush = ShaderBrush(
    ImageShader(textureBitmap, TileMode.Repeated, TileMode.Repeated)
)
Canvas(Modifier.fillMaxSize()) {
    drawRect(brush = shaderBrush)
}
```

### Sliders em tempo real
```kotlin
var lapelaValue by remember { mutableStateOf(50f) }
val lapelaScale by animateFloatAsState(
    targetValue = 0.5f + (lapelaValue / 100f) * 1.0f,
    animationSpec = tween(100)
)

Slider(value = lapelaValue, onValueChange = { lapelaValue = it }, valueRange = 0f..100f)

Image(
    painter = painterResource(R.drawable.layer_front_lapel),
    modifier = Modifier.graphicsLayer { scaleX = lapelaScale }
)
```

---

## FASE 3 — Preview Pseudo-3D → Compose

### Swipe + Crossfade entre ângulos
```kotlin
val pagerState = rememberPagerState(pageCount = { 3 })

HorizontalPager(state = pagerState) { page ->
    val angle = PreviewAngle.values()[page]
    AnimatedContent(
        targetState = angle,
        transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(250)) }
    ) { a ->
        MannequinPreview(angle = a, state = editorState)
    }
}
HorizontalPagerIndicator(pagerState = pagerState, pageCount = 3)
```

### Zoom (Pinch-to-zoom)
```kotlin
var scale by remember { mutableStateOf(1f) }
val transformableState = rememberTransformableState { zoomChange, _, _ ->
    scale = (scale * zoomChange).coerceIn(1f, 2.5f)
}
Box(Modifier.transformable(transformableState).graphicsLayer { scaleX = scale; scaleY = scale }) {
    MannequinPreview(...)
}
```

---

## FASE 4 — Relatório → Compose

```kotlin
// Gerar dados do relatório a partir do estado
data class EditorReport(
    val layers: List<EditorLayer>,
    val features: Map<String, String>,
    val performance: PerformanceMetrics,
    val generatedAt: String
)

@Composable
fun ReportScreen(viewModel: EditorViewModel) {
    val state by viewModel.state.collectAsState()
    val report = viewModel.buildReport(state)
    LazyColumn {
        item { ReportHeader(report) }
        items(report.layers) { LayerReportRow(it) }
        item { PerformanceSection(report.performance) }
    }
}
```

---

## Estrutura de ficheiros sugerida no projecto KMP

```
composeApp/src/commonMain/kotlin/com/suitup/
└── editor/
    ├── EditorScreen.kt          ← Voyager Screen principal
    ├── EditorViewModel.kt       ← StateFlow + ScreenModel
    ├── EditorConfig.kt          ← Constantes (cores, tecidos, layers)
    ├── layers/
    │   ├── EditorLayer.kt
    │   ├── LayerManager.kt
    │   └── SuitLayerStack.kt    ← Composable
    ├── customization/
    │   ├── SuitColorOption.kt
    │   ├── FabricOption.kt
    │   ├── EditorSlider.kt
    │   └── CustomizationPanel.kt ← Composable (painel direito)
    ├── preview/
    │   ├── PreviewAngle.kt
    │   ├── MannequinPreview.kt  ← Composable (canvas central)
    │   └── AnglePreviewPager.kt ← HorizontalPager wrapper
    └── report/
        ├── EditorReport.kt
        └── ReportScreen.kt      ← Voyager Screen
```
