/* ================================================================
   state.js — Gestão de estado global reativo
   Suit Editor 3D

   Padrão Observer: qualquer módulo pode subscrever mudanças via
     AppState.subscribe(callback)

   INTEGRAÇÃO ANDROID/COMPOSE:
     Substituir por StateFlow ou ViewModel com LiveData:
       class SuitEditorViewModel : ViewModel() {
         private val _state = MutableStateFlow(SuitEditorState())
         val state: StateFlow<SuitEditorState> = _state
         fun updateColor(colorId: String) { _state.update { it.copy(selectedColor = colorId) } }
       }
================================================================ */

'use strict';

const AppState = (() => {

  // ----------------------------------------------------------------
  // Estado inicial
  // ----------------------------------------------------------------
  let _state = {
    currentPhase:    1,
    activeAngle:     0,           // índice em ANGLES[]
    zoomLevel:       1,           // 1 = normal, 2 = zoom
    selectedColor:   COLOR_PALETTE[0].id,
    selectedFabric:  FABRIC_CATALOG[0].id,
    selectedLayer:   null,        // id da layer seleccionada no painel
    layerVisibility: Object.fromEntries(
      LAYER_DEFINITIONS.map(l => [l.id, l.on])
    ),
    sliders: Object.fromEntries(
      SLIDER_DEFINITIONS.map(s => [s.id, s.value])
    ),
    composition: {
      validated:      false,
      lastValidation: null,
      issues:         [],
    },
    performance: {
      fps:      60,
      renderMs: 12,
      apkKb:    0,
    },
  };

  // Lista de callbacks subscritores
  const _listeners = [];

  // ----------------------------------------------------------------
  // Notificar todos os subscritores com cópia do estado
  // ----------------------------------------------------------------
  function _notify() {
    const snapshot = JSON.parse(JSON.stringify(_state));
    _listeners.forEach(fn => fn(snapshot));
  }

  // ----------------------------------------------------------------
  // API pública do AppState
  // ----------------------------------------------------------------
  return {

    // Ler estado (cópia imutável)
    get() {
      return JSON.parse(JSON.stringify(_state));
    },

    // Actualizar campos parcialmente e notificar
    // Uso: AppState.set({ selectedColor: 'navy' })
    set(partial) {
      Object.assign(_state, partial);
      _notify();
    },

    // Subscribir mudanças
    // Uso: AppState.subscribe(state => console.log(state))
    subscribe(fn) {
      _listeners.push(fn);
    },

    // Actualizar visibilidade de uma layer específica
    setLayerVisible(layerId, visible) {
      _state.layerVisibility[layerId] = visible;
      _notify();
    },

    // Actualizar valor de um slider
    setSlider(sliderId, value) {
      _state.sliders[sliderId] = value;
      _notify();
    },

    // Actualizar métricas de performance (chamado pelo sistema de render)
    updatePerformance(fps, renderMs, apkKb) {
      _state.performance = { fps, renderMs, apkKb };
      _notify();
    },

    // Restaurar estado completo (ex: vindo de servidor ou SharedPreferences)
    restore(savedState) {
      _state = Object.assign(_state, savedState);
      _notify();
    },
  };

})();
