/* ================================================================
   config.js — Constantes de configuração do Suit Editor 3D
   Fase: transversal (todas as fases dependem deste ficheiro)

   INTEGRAÇÃO ANDROID:
     Mover estas constantes para um ficheiro Kotlin:
       object SuitEditorConfig { ... }
     ou para um JSON carregado via AssetManager.
================================================================ */

'use strict';

// ----------------------------------------------------------------
// ASSETS DO MANEQUIM — FASE 3
// INTEGRAÇÃO: substituir por URLs reais dos PNGs exportados na FASE 1
//   Formato: mannequin_{angle}@{scale}x.png (transparent background)
//   Resolução recomendada: front 400x720px @2x, 600x1080px @3x
// ----------------------------------------------------------------
const MANNEQUIN_ASSETS = {
  front: {
    '1x': 'assets/mannequin_front.png',
    '2x': 'assets/mannequin_front@2x.png',
    '3x': 'assets/mannequin_front@3x.png',
  },
  side: {
    '1x': 'assets/mannequin_side.png',
    '2x': 'assets/mannequin_side@2x.png',
    '3x': 'assets/mannequin_side@3x.png',
  },
  back: {
    '1x': 'assets/mannequin_back.png',
    '2x': 'assets/mannequin_back@2x.png',
    '3x': 'assets/mannequin_back@3x.png',
  },
};

// ----------------------------------------------------------------
// PALETA DE CORES — FASE 2: ColorFilter
// ≥ 8 cores selecionáveis (critério da fase)
// INTEGRAÇÃO ANDROID:
//   val COLOR_PALETTE = listOf(
//     SuitColor("navy", Color(0xFF1A2340), "Navy"),
//     ...
//   )
//   Aplicar via: paint.colorFilter = LightingColorFilter(color.toArgb(), 0)
// ----------------------------------------------------------------
const COLOR_PALETTE = [
  { id: 'navy',     hex: '#1a2340', label: 'Navy'     },
  { id: 'charcoal', hex: '#2d2d2d', label: 'Charcoal' },
  { id: 'black',    hex: '#0d0d0d', label: 'Black'    },
  { id: 'cream',    hex: '#e8e0d0', label: 'Cream'    },
  { id: 'grey',     hex: '#707070', label: 'Grey'     },
  { id: 'wine',     hex: '#5c1a2a', label: 'Wine'     },
  { id: 'forest',   hex: '#1a3a28', label: 'Forest'   },
  { id: 'camel',    hex: '#c8a87a', label: 'Camel'    },
  { id: 'cobalt',   hex: '#1a3a7a', label: 'Cobalt'   },
  { id: 'burgundy', hex: '#4a0e1a', label: 'Burgundy' },
  { id: 'ivory',    hex: '#f0ead8', label: 'Ivory'    },
  { id: 'teal',     hex: '#0f3d3a', label: 'Teal'     },
  { id: 'rust',     hex: '#7a2a1a', label: 'Rust'     },
  { id: 'olive',    hex: '#3d4a1a', label: 'Olive'    },
  { id: 'slate',    hex: '#3a4a5c', label: 'Slate'    },
  { id: 'sand',     hex: '#d4c4a0', label: 'Sand'     },
];

// ----------------------------------------------------------------
// CATÁLOGO DE TECIDOS — FASE 2: BitmapShader
// INTEGRAÇÃO ANDROID:
//   val FABRIC_CATALOG = listOf(
//     Fabric("wool", R.drawable.texture_wool, "Wool"),
//     ...
//   )
//   Aplicar via:
//     val bitmap = BitmapFactory.decodeResource(resources, fabric.textureRes)
//     val shader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
//     paint.shader = shader
// ----------------------------------------------------------------
const FABRIC_CATALOG = [
  {
    id: 'wool',
    label: 'Wool',
    texturePath: 'assets/textures/wool.png',
    // Prévia CSS — substituir por textura real na integração
    previewBg: '#2a2a2a',
    previewPattern: 'repeating-linear-gradient(45deg,#333 0,#333 1px,#2a2a2a 0,#2a2a2a 50%)',
  },
  {
    id: 'linen',
    label: 'Linen',
    texturePath: 'assets/textures/linen.png',
    previewBg: '#c8b890',
    previewPattern: 'repeating-linear-gradient(0deg,#bfaf85 0,#bfaf85 1px,#c8b890 0,#c8b890 4px)',
  },
  {
    id: 'silk',
    label: 'Silk',
    texturePath: 'assets/textures/silk.png',
    previewBg: '#3a3a50',
    previewPattern: 'repeating-linear-gradient(135deg,#44445a 0,#44445a 1px,#3a3a50 0,#3a3a50 6px)',
  },
  {
    id: 'tweed',
    label: 'Tweed',
    texturePath: 'assets/textures/tweed.png',
    previewBg: '#4a4a3a',
    previewPattern: 'repeating-linear-gradient(45deg,#555545 0,#555545 1px,#4a4a3a 0,#4a4a3a 3px)',
  },
  {
    id: 'velvet',
    label: 'Velvet',
    texturePath: 'assets/textures/velvet.png',
    previewBg: '#2a1a2e',
    previewPattern: 'radial-gradient(circle at 1px 1px,#3a2a3e 1px,#2a1a2e 0)',
  },
  {
    id: 'cotton',
    label: 'Cotton',
    texturePath: 'assets/textures/cotton.png',
    previewBg: '#e8e0d0',
    previewPattern: 'repeating-linear-gradient(90deg,#ddd8c8 0,#ddd8c8 1px,#e8e0d0 0,#e8e0d0 5px)',
  },
];

// ----------------------------------------------------------------
// DEFINIÇÃO DAS LAYERS — FASE 1
// Z-order: base (0) → overlays → UI (topo)
// INTEGRAÇÃO ANDROID/COMPOSE:
//   data class LayerDef(val id: String, val label: String, val z: Int, val on: Boolean)
//   Usar Canvas.save() / Canvas.restore() com Z-order explícito
// ----------------------------------------------------------------
const LAYER_DEFINITIONS = [
  { id: 'base',    label: 'Base Silhueta', z: 0, on: true,  color: '#4a4a6a' },
  { id: 'fato',    label: 'Fato Base',     z: 1, on: true,  color: '#5a6a8a' },
  { id: 'lapela',  label: 'Lapela',        z: 2, on: true,  color: '#888888' },
  { id: 'gola',    label: 'Gola',          z: 3, on: true,  color: '#666666' },
  { id: 'botoes',  label: 'Botões',        z: 4, on: true,  color: '#aaaaaa' },
  { id: 'bolso',   label: 'Bolso',         z: 5, on: true,  color: '#777777' },
  { id: 'forro',   label: 'Forro',         z: 6, on: false, color: '#c8a87a' },
];

// ----------------------------------------------------------------
// SLIDERS — FASE 2
// Afetam a layer correta em tempo real (critério da fase)
// INTEGRAÇÃO ANDROID/COMPOSE:
//   var lapelaValue by remember { mutableStateOf(50f) }
//   Slider(value = lapelaValue, onValueChange = { v ->
//     lapelaValue = v
//     applyLapelaSlider(v)   // atualiza Composable da lapela
//   })
// ----------------------------------------------------------------
const SLIDER_DEFINITIONS = [
  {
    id: 'lapela',
    label: 'Lapela',
    min: 0, max: 100, value: 50,
    unit: '%',
    targetLayer: 'lapela',  // layer afetada
    // INTEGRAÇÃO: propriedade Compose afetada → scaleX do Modifier.graphicsLayer{}
    composeProperty: 'scaleX',
  },
  {
    id: 'comprimento',
    label: 'Comprimento',
    min: 0, max: 100, value: 60,
    unit: '%',
    targetLayer: 'fato',
    composeProperty: 'scaleY',
  },
  {
    id: 'ombro',
    label: 'Ombro',
    min: 0, max: 100, value: 45,
    unit: '%',
    targetLayer: 'fato',
    composeProperty: 'scaleX',
  },
];

// ----------------------------------------------------------------
// ÂNGULOS DO PREVIEW — FASE 3
// INTEGRAÇÃO: mapear para HorizontalPager(pageCount = ANGLES.length)
// ----------------------------------------------------------------
const ANGLES = ['FRENTE', 'LADO', 'COSTAS'];

// ----------------------------------------------------------------
// CONFIGURAÇÕES DE ANIMAÇÃO — FASE 3
// ----------------------------------------------------------------
const ANIM_CONFIG = {
  crossfadeDurationMs: 250,   // duração do crossfade entre ângulos
  zoomScale: 1.6,             // factor de zoom no FAB
  swipeThresholdPx: 40,       // mínimo px para reconhecer swipe
};
