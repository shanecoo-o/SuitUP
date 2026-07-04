/* ================================================================
   app.js — Orquestrador principal do Suit Editor 3D
   Boot, phase tabs, API pública global

   INTEGRAÇÃO ANDROID/COMPOSE:
     Este ficheiro equivale ao MainActivity.kt + SuitEditorApp composable:
       @Composable fun SuitEditorApp() {
         val viewModel: SuitEditorViewModel = viewModel()
         SuitEditorTheme {
           Scaffold { padding ->
             Row {
               LayerPanel(viewModel)      // FASE 1
               PreviewPane(viewModel)     // FASE 3
               CustomizationPanel(viewModel) // FASE 2
             }
           }
         }
       }
================================================================ */

'use strict';

// ----------------------------------------------------------------
// Renderizar tabs de fase na topbar
// ----------------------------------------------------------------
function _renderPhaseTabs() {
  const container = document.getElementById('phaseTabs');
  if (!container) return;
  container.innerHTML = '';

  const labels = { 1: 'F1 LAYERS', 2: 'F2 CUSTOM', 3: 'F3 PREVIEW', 4: 'F4 REPORT' };
  const state  = AppState.get();

  [1, 2, 3, 4].forEach(n => {
    const pill = document.createElement('div');
    pill.className = 'phase-pill' + (state.currentPhase === n ? ' active' : '');
    pill.textContent = labels[n];
    pill.onclick = () => {
      AppState.set({ currentPhase: n });
      _renderPhaseTabs();
      const statusMap = {
        1: 'LAYERS & ASSETS ACTIVA',
        2: 'CUSTOMIZAÇÃO ACTIVA',
        3: 'PREVIEW 3D ACTIVA',
        4: 'RELATÓRIO ACTIVO',
      };
      const statusEl = document.getElementById('statusText');
      if (statusEl) statusEl.textContent = statusMap[n];
    };
    container.appendChild(pill);
  });
}

// ----------------------------------------------------------------
// API PÚBLICA — window.SuitEditorApp
// Ponto de integração para componentes externos e testes
//
// Uso de exemplo:
//   SuitEditorApp.applyColor('navy')
//   SuitEditorApp.setAngle(1)
//   const md = SuitEditorApp.exportMarkdown()
//   SuitEditorApp.onUpdate(state => console.log(state))
// ----------------------------------------------------------------
window.SuitEditorApp = {

  // ---- Cores (FASE 2) ----
  applyColor:   (colorId)  => ColorFilter.applyColor(colorId),
  applyFabric:  (fabricId) => BitmapShader.applyFabric(fabricId),

  // ---- Sliders (FASE 2) ----
  setSlider:    (id, val)  => SliderController.setValue(id, val),

  // ---- Preview / ângulos (FASE 3) ----
  setAngle:     (i)        => PreviewController.crossfadeTo(i),
  nextAngle:    ()         => PreviewController.nextAngle(),
  prevAngle:    ()         => PreviewController.prevAngle(),
  toggleZoom:   ()         => PreviewController.toggleZoom(),
  exportPNG:    ()         => PreviewController.exportPNG(),

  // Ir para ângulo por nome: 'FRENTE' | 'LADO' | 'COSTAS'
  setAngleByName(name) {
    const i = ANGLES.indexOf(name.toUpperCase());
    if (i >= 0) PreviewController.crossfadeTo(i);
    else console.warn('[SuitEditorApp] Ângulo desconhecido:', name);
  },

  // ---- Layers (FASE 1) ----
  validateComposition: () => LayerManager.validateComposition(),

  // ---- Relatório (FASE 4) ----
  openReport:      () => ReportEngine.open(),
  closeReport:     () => ReportEngine.close(),
  exportReport:    () => ReportEngine.generate(),
  exportMarkdown:  () => ReportEngine.toMarkdown(),
  downloadReport:  () => ReportEngine.download(),

  // ---- Estado ----
  getState:  () => AppState.get(),

  // Restaurar estado completo (ex: sincronizar com backend ou SharedPreferences)
  setState(newState) {
    AppState.restore(newState);
    LayerManager.renderTree();
    if (newState.selectedColor) ColorFilter.applyColor(newState.selectedColor);
    if (newState.selectedFabric) BitmapShader.applyFabric(newState.selectedFabric);
    if (newState.sliders) {
      Object.entries(newState.sliders).forEach(([id, val]) => {
        SliderController.setValue(id, val);
      });
    }
  },

  // Subscrever actualizações de estado
  onUpdate: (fn) => AppState.subscribe(fn),
};

// ----------------------------------------------------------------
// BOOT — Ordem obrigatória: Fase 1 → 2 → 3 (4 é on-demand)
// ----------------------------------------------------------------
document.addEventListener('DOMContentLoaded', () => {
  console.log('=== SUIT EDITOR 3D — Boot ===');

  // Tabs de fase
  _renderPhaseTabs();

  // FASE 1 — LayerManager
  LayerManager.init();

  // FASE 2 — Customização
  ColorFilter.init();
  BitmapShader.init();
  SliderController.init();

  // FASE 3 — Preview
  PreviewController.init();

  // FASE 4 — Report (pronto, abre on-demand)
  ReportEngine.init();

  console.log('=== Boot completo ===');
  console.log('[API] window.SuitEditorApp disponível');
  console.log('[API] Métodos:', Object.keys(window.SuitEditorApp).join(', '));
  console.log('');
  console.log('[INTEGRAÇÃO PENDENTE]');
  console.log('  → Substituir SVG por PNGs @2x @3x em assets/');
  console.log('  → ColorFilter CSS → Android LightingColorFilter');
  console.log('  → BitmapShader CSS → Android BitmapShader(bitmap, REPEAT, REPEAT)');
  console.log('  → CSS transform → Compose Modifier.graphicsLayer{}');
  console.log('  → Export PNG → Canvas.drawToBitmap() + FileOutputStream');
});
