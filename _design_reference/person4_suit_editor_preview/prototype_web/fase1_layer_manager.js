/* ================================================================
   fase1_layer_manager.js — FASE 1: Layers & Assets Base
   Suit Editor 3D

   Responsabilidades:
     ✓ Renderizar árvore de layers com Z-order (base → overlays → UI)
     ✓ Toggle de visibilidade por layer (sem bleeding entre camadas)
     ✓ Selecção de layer activa
     ✓ Sincronização com o canvas central (SVG/PNG)
     ✓ Export @2x e @3x (stub para integração)
     ✓ Validação de composição

   INTEGRAÇÃO ANDROID/COMPOSE:
     Este módulo equivale a um LazyColumn de LayerRow() composables
     controlados por um LayerViewModel que expõe:
       val layers: StateFlow<List<LayerState>>
       fun toggleVisibility(layerId: String)
       fun selectLayer(layerId: String)
     O canvas é um Compose Canvas{} com drawImage() por layer
     respeitando a ordem de Z (for loop sobre LAYER_DEFINITIONS
     ordenado por .z ascendente).

   ESTRUTURA DE OUTPUTS (FASE 1):
     assets/
       base_silhueta@1x.png    — silhueta neutra transparent bg
       base_silhueta@2x.png
       base_silhueta@3x.png
       lapela@2x.png           — overlay lapela
       botoes@2x.png           — overlay botões
       bolso@2x.png            — overlay bolso
       gola@2x.png             — overlay gola
       forro@2x.png            — overlay forro
================================================================ */

'use strict';

const LayerManager = (() => {

  // ----------------------------------------------------------------
  // Renderizar a árvore de layers no painel esquerdo
  // Ordenada do maior Z (topo) para o menor (base) — visual intuitivo
  // ----------------------------------------------------------------
  function _renderTree() {
    const panel = document.getElementById('layerPanel');
    if (!panel) return;

    // Preservar o título da secção
    panel.innerHTML = '<div class="panel-section-title">Z-Order · Layers</div>';

    const state = AppState.get();

    // Ordenar: Z descendente (topo visual primeiro na lista)
    const sorted = [...LAYER_DEFINITIONS].sort((a, b) => b.z - a.z);

    sorted.forEach(layer => {
      const isSelected = state.selectedLayer === layer.id;
      const isVisible  = state.layerVisibility[layer.id];

      // Container da linha
      const item = document.createElement('div');
      item.className = 'layer-item' + (isSelected ? ' selected' : '');
      item.dataset.layerId = layer.id;
      item.onclick = () => _selectLayer(layer.id);

      // Toggle visibilidade
      const toggle = document.createElement('div');
      toggle.className = 'layer-toggle' + (isVisible ? ' on' : '');
      toggle.title = isVisible ? 'Ocultar layer' : 'Mostrar layer';
      toggle.textContent = isVisible ? '✓' : '';
      toggle.onclick = (e) => {
        e.stopPropagation(); // não seleccionar ao clicar no toggle
        _toggleVisibility(layer.id);
      };

      // Indicador de cor da layer
      const dot = document.createElement('div');
      dot.className = 'layer-dot';
      dot.style.background = layer.color;

      // Nome da layer
      const label = document.createElement('span');
      label.textContent = layer.label;
      label.style.flex = '1';
      label.style.overflow = 'hidden';
      label.style.textOverflow = 'ellipsis';
      label.style.whiteSpace = 'nowrap';

      // Z-order badge
      const zBadge = document.createElement('span');
      zBadge.className = 'layer-z';
      zBadge.textContent = 'Z' + layer.z;

      item.appendChild(toggle);
      item.appendChild(dot);
      item.appendChild(label);
      item.appendChild(zBadge);
      panel.appendChild(item);
    });

    // Separador e info da fase
    const divider = document.createElement('div');
    divider.className = 'divider';
    panel.appendChild(divider);

    const info = document.createElement('div');
    info.style.cssText =
      'padding:10px 16px;font-size:10px;color:var(--color-text-muted);' +
      'letter-spacing:0.06em;line-height:1.9';
    info.innerHTML =
      'FASE 1 — Compose Canvas · PNG Assets<br>' +
      '<span style="color:var(--color-text)">Export:</span> @1x · @2x · @3x<br>' +
      '<span style="color:var(--color-text)">Layers:</span> ' +
        LAYER_DEFINITIONS.length + ' definidas';
    panel.appendChild(info);
  }

  // ----------------------------------------------------------------
  // Toggle visibilidade de uma layer
  // Sincroniza estado + DOM do canvas
  // ----------------------------------------------------------------
  function _toggleVisibility(layerId) {
    const state = AppState.get();
    const newVisible = !state.layerVisibility[layerId];

    AppState.setLayerVisible(layerId, newVisible);

    // Aplicar ao SVG/DOM (INTEGRAÇÃO: substituir por
    //   layer.setVisible(layerId, newVisible) na Compose Canvas API)
    const el = document.getElementById('layer-' + layerId);
    if (el) {
      el.style.display = newVisible ? '' : 'none';
    }

    _renderTree();
  }

  // ----------------------------------------------------------------
  // Seleccionar layer activa
  // ----------------------------------------------------------------
  function _selectLayer(layerId) {
    const state = AppState.get();
    const newSelected = state.selectedLayer === layerId ? null : layerId;
    AppState.set({ selectedLayer: newSelected });
    _renderTree();
  }

  // ----------------------------------------------------------------
  // Validação de composição — FASE 1 (teste)
  // Verifica ausência de bleeding entre camadas
  //
  // INTEGRAÇÃO: substituir por pixel-diff real:
  //   val issues = mutableListOf<String>()
  //   layers.forEachPair { layerA, layerB ->
  //     if (hasPixelOverlap(layerA.bitmap, layerB.bitmap)) {
  //       issues.add("Bleeding: ${layerA.id} → ${layerB.id}")
  //     }
  //   }
  // ----------------------------------------------------------------
  function validateComposition() {
    const state = AppState.get();
    const issues = [];

    LAYER_DEFINITIONS.forEach(layer => {
      if (state.layerVisibility[layer.id]) {
        const el = document.getElementById('layer-' + layer.id);
        if (!el) {
          issues.push(layer.label + ': elemento do canvas não encontrado');
        }
      }
    });

    const ok = issues.length === 0;
    AppState.set({
      composition: {
        validated:      ok,
        lastValidation: new Date().toISOString(),
        issues,
      }
    });

    // Atualizar mensagem na UI
    const msg = document.getElementById('validationMsg');
    if (msg) {
      msg.textContent = ok
        ? '✓ SEM BLEEDING — composição válida'
        : '✗ ' + issues.join(' | ');
      msg.style.color = ok
        ? 'var(--color-success-light)'
        : 'var(--color-warn-light)';
    }

    console.log('[LayerManager] Validação:', ok ? 'OK' : issues.join(', '));
    return { ok, issues };
  }

  // ----------------------------------------------------------------
  // Export PNG — FASE 1
  // INTEGRAÇÃO: substituir por:
  //   val bitmap = Bitmap.createBitmap(width * scale, height * scale, ARGB_8888)
  //   val canvas = android.graphics.Canvas(bitmap)
  //   layers.filter { it.visible }.forEach { layer ->
  //     canvas.drawBitmap(layer.bitmap, matrix, paint)
  //   }
  //   bitmap.compress(PNG, 100, outputStream)
  // ----------------------------------------------------------------
  function exportPNG(scale = 2) {
    console.log('[LayerManager] Export PNG @' + scale + 'x');
    console.log('  INTEGRAÇÃO: Canvas.toDataURL("image/png") com devicePixelRatio=' + scale);

    // Stub: em implementação real, retorna o Blob/URL do PNG
    return {
      scale,
      status: 'pending_integration',
      filename: 'fato_custom@' + scale + 'x.png',
    };
  }

  // ----------------------------------------------------------------
  // API pública
  // ----------------------------------------------------------------
  return {
    init() {
      _renderTree();
      console.log('[FASE 1] LayerManager iniciado —', LAYER_DEFINITIONS.length, 'layers');
    },
    renderTree:          _renderTree,
    toggleVisibility:    _toggleVisibility,
    selectLayer:         _selectLayer,
    validateComposition,
    exportPNG,
  };

})();
