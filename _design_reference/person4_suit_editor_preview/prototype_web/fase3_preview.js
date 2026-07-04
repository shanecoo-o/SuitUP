/* ================================================================
   fase3_preview.js — FASE 3: Preview 3D Fake
   Suit Editor 3D

   Responsabilidades:
     ✓ Manequim PNG transparente (front, side, back)
     ✓ Composição de imagem por ângulo com fato customizado
     ✓ Swipe horizontal entre 3 ângulos + indicador activo
     ✓ Pinch-to-zoom + FABs flutuantes
     ✓ Animação de transição entre ângulos (crossfade)
     ✓ Preview sincronizado com Editor 2D em tempo real (critério)

   INTEGRAÇÃO ANDROID/COMPOSE:
     Swipe      → HorizontalPager(pageCount = 3, state = pagerState)
     Crossfade  → AnimatedContent(targetState = angle) { Crossfade(it) }
     Zoom       → Modifier.transformable(state = transformableState)
                  + rememberTransformableState { zoom, pan, rot → ... }
     FAB        → FloatingActionButton { Icon(...) }
     Indicador  → PagerIndicator(pagerState, pageCount = 3)
     Sync 2D    → LaunchedEffect(editorState) { pager.animateScrollToPage(...) }

   ASSETS NECESSÁRIOS (FASE 1):
     assets/mannequin_front@2x.png  — manequim frente, fundo transparent
     assets/mannequin_side@2x.png   — manequim lado
     assets/mannequin_back@2x.png   — manequim costas
     (+ @1x e @3x para cada ângulo)
================================================================ */

'use strict';

const PreviewController = (() => {

  let _touchStartX = 0;
  let _touchStartY = 0;
  let _pinchStartDist = 0;

  // ----------------------------------------------------------------
  // Inicialização — eventos de swipe e pinch
  // ----------------------------------------------------------------
  function _initGestures() {
    const viewport = document.getElementById('mannequinViewport');
    if (!viewport) return;

    // ---- Swipe horizontal (touch) ----
    // INTEGRAÇÃO: substituir por HorizontalPager com swipe nativo do Compose
    viewport.addEventListener('touchstart', (e) => {
      if (e.touches.length === 1) {
        _touchStartX = e.touches[0].clientX;
        _touchStartY = e.touches[0].clientY;
      } else if (e.touches.length === 2) {
        // Início de pinch
        _pinchStartDist = _getTouchDist(e.touches);
      }
    }, { passive: true });

    viewport.addEventListener('touchend', (e) => {
      if (e.changedTouches.length === 1 && e.touches.length === 0) {
        const dx = e.changedTouches[0].clientX - _touchStartX;
        const dy = e.changedTouches[0].clientY - _touchStartY;

        // Só reconhecer swipe se predominantemente horizontal
        if (Math.abs(dx) > ANIM_CONFIG.swipeThresholdPx &&
            Math.abs(dx) > Math.abs(dy) * 1.5) {
          if (dx < 0) _goNextAngle();
          else _goPrevAngle();
        }
      }
    }, { passive: true });

    // ---- Pinch-to-zoom (touch) ----
    // INTEGRAÇÃO: Modifier.transformable com rememberTransformableState
    viewport.addEventListener('touchmove', (e) => {
      if (e.touches.length === 2) {
        e.preventDefault();
        const dist = _getTouchDist(e.touches);
        const ratio = dist / (_pinchStartDist || dist);

        const state = AppState.get();
        const stage = document.getElementById('mannequinStage');
        if (stage && ratio > 1.3 && state.zoomLevel === 1) {
          _applyZoom(2);
        } else if (stage && ratio < 0.7 && state.zoomLevel === 2) {
          _applyZoom(1);
        }
      }
    }, { passive: false });

    // ---- Swipe com mouse (desktop) ----
    let mouseStartX = 0;
    let isDragging = false;
    viewport.addEventListener('mousedown', (e) => {
      mouseStartX = e.clientX;
      isDragging = true;
    });
    viewport.addEventListener('mouseup', (e) => {
      if (!isDragging) return;
      isDragging = false;
      const dx = e.clientX - mouseStartX;
      if (Math.abs(dx) > ANIM_CONFIG.swipeThresholdPx) {
        if (dx < 0) _goNextAngle();
        else _goPrevAngle();
      }
    });
    viewport.addEventListener('mouseleave', () => { isDragging = false; });
  }

  // ----------------------------------------------------------------
  // Distância entre dois pontos de toque (para pinch)
  // ----------------------------------------------------------------
  function _getTouchDist(touches) {
    const dx = touches[0].clientX - touches[1].clientX;
    const dy = touches[0].clientY - touches[1].clientY;
    return Math.sqrt(dx * dx + dy * dy);
  }

  // ----------------------------------------------------------------
  // Atualizar indicadores de ângulo (dots)
  // ----------------------------------------------------------------
  function _updateIndicator(angleIndex) {
    document.querySelectorAll('.angle-dot').forEach((dot, i) => {
      dot.classList.toggle('active', i === angleIndex);
    });
    const label = document.getElementById('angleLabel');
    if (label) label.textContent = ANGLES[angleIndex];
  }

  // ----------------------------------------------------------------
  // Crossfade entre ângulos — FASE 3 (critério de animação)
  // INTEGRAÇÃO: substituir por AnimatedContent + Crossfade do Compose:
  //   AnimatedContent(
  //     targetState = currentAngle,
  //     transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(250)) }
  //   ) { angle ->
  //     MannequinView(angle = angle, customization = editorState)
  //   }
  // ----------------------------------------------------------------
  function _crossfadeTo(angleIndex) {
    const state = AppState.get();
    if (angleIndex === state.activeAngle) return;

    const stage = document.getElementById('mannequinStage');
    if (!stage) return;

    // Fase 1 do crossfade: fade out
    stage.classList.add('transitioning');

    setTimeout(() => {
      AppState.set({ activeAngle: angleIndex });

      // INTEGRAÇÃO: aqui trocar o src dos <img> de layer:
      //   document.getElementById('layer-base').src = MANNEQUIN_ASSETS[angleName]['2x']
      //   (repetir para cada layer overlay)
      const angleName = ['front', 'side', 'back'][angleIndex];
      console.log('[PreviewController] Ângulo:', ANGLES[angleIndex],
        '→ assets:', MANNEQUIN_ASSETS[angleName]['2x']);

      _updateIndicator(angleIndex);

      // Fase 2 do crossfade: fade in
      stage.classList.remove('transitioning');

    }, ANIM_CONFIG.crossfadeDurationMs);
  }

  // ----------------------------------------------------------------
  // Navegação de ângulo
  // ----------------------------------------------------------------
  function _goNextAngle() {
    const state = AppState.get();
    _crossfadeTo((state.activeAngle + 1) % ANGLES.length);
  }

  function _goPrevAngle() {
    const state = AppState.get();
    _crossfadeTo((state.activeAngle - 1 + ANGLES.length) % ANGLES.length);
  }

  // ----------------------------------------------------------------
  // Zoom — FAB e pinch
  // INTEGRAÇÃO: var scale by remember { mutableStateOf(1f) }
  //   Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
  // ----------------------------------------------------------------
  function _applyZoom(level) {
    AppState.set({ zoomLevel: level });

    const stage = document.getElementById('mannequinStage');
    if (stage) {
      stage.style.transform  = level === 2
        ? 'scale(' + ANIM_CONFIG.zoomScale + ')'
        : 'scale(1)';
      stage.style.transition = 'transform 0.3s ease';
    }

    const fabZoom = document.getElementById('fabZoom');
    if (fabZoom) fabZoom.textContent = level === 2 ? '⊖' : '⊕';

    console.log('[PreviewController] Zoom:', level + 'x');
  }

  // ----------------------------------------------------------------
  // Export PNG do ângulo actual — delega à FASE 1
  // INTEGRAÇÃO: val bitmap = composeView.toBitmap(scale = 2)
  //             bitmap.compress(PNG, 100, FileOutputStream(outputFile))
  // ----------------------------------------------------------------
  function _exportPNG() {
    const state = AppState.get();
    const angle = ANGLES[state.activeAngle].toLowerCase();
    console.log('[PreviewController] Export PNG — ângulo:', angle);
    return LayerManager.exportPNG(2);
  }

  // ----------------------------------------------------------------
  // Sincronização com Editor 2D em tempo real (critério FASE 3)
  // Chamado sempre que o estado muda (subscrição ao AppState)
  // INTEGRAÇÃO: LaunchedEffect(editorViewModel.state) { ... }
  // ----------------------------------------------------------------
  function _syncWithEditor(state) {
    // Aplicar cor actual ao preview
    const c = COLOR_PALETTE.find(p => p.id === state.selectedColor);
    if (c) {
      const fatoLayer = document.getElementById('layer-fato');
      if (fatoLayer) {
        fatoLayer.querySelectorAll('rect').forEach(r => r.setAttribute('fill', c.hex));
      }
    }

    // Aplicar visibilidade das layers
    LAYER_DEFINITIONS.forEach(layer => {
      const el = document.getElementById('layer-' + layer.id);
      if (el) {
        el.style.display = state.layerVisibility[layer.id] ? '' : 'none';
      }
    });
  }

  // ----------------------------------------------------------------
  // API pública
  // ----------------------------------------------------------------
  return {
    init() {
      _initGestures();
      _updateIndicator(0);

      // Subscrever mudanças de estado para sincronização em tempo real
      AppState.subscribe(_syncWithEditor);

      console.log('[FASE 3] PreviewController iniciado — ângulos:', ANGLES.join(', '));
    },

    crossfadeTo:  _crossfadeTo,
    nextAngle:    _goNextAngle,
    prevAngle:    _goPrevAngle,
    toggleZoom() {
      const state = AppState.get();
      _applyZoom(state.zoomLevel === 1 ? 2 : 1);
    },
    exportPNG: _exportPNG,
  };

})();
