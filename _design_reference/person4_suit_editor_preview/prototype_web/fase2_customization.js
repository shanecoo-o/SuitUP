/* ================================================================
   fase2_customization.js — FASE 2: Customização (cores, tecidos, sliders)
   Suit Editor 3D

   Responsabilidades:
     ✓ Paleta com ≥ 8 cores selecionáveis via ColorFilter
     ✓ Catálogo de tecidos via BitmapShader
     ✓ Sliders: lapela, comprimento, ombro
     ✓ Sliders afetam layer correta em tempo real (critério)

   INTEGRAÇÃO ANDROID/COMPOSE:
     ColorFilter  → paint.colorFilter = LightingColorFilter(color, 0)
                    ou Modifier.graphicsLayer { colorFilter = BlendModeColorFilter(color, SrcIn) }
     BitmapShader → val shader = BitmapShader(textureBitmap, REPEAT, REPEAT)
                    paint.shader = shader
     Sliders      → Slider(value, onValueChange) + animateFloatAsState(targetValue = v)
================================================================ */

'use strict';

// ----------------------------------------------------------------
// ColorFilter — FASE 2
// Aplica cor à layer base via ColorFilter (CSS fill para preview web)
// ----------------------------------------------------------------
const ColorFilter = (() => {

  function _applyToDOM(hex) {
    // INTEGRAÇÃO: substituir por Android LightingColorFilter:
    //   val filter = LightingColorFilter(Color.parseColor(hex), 0)
    //   paint.colorFilter = filter
    //   canvas.drawBitmap(fatoBaseBitmap, 0f, 0f, paint)
    const fatoLayer = document.getElementById('layer-fato');
    if (!fatoLayer) return;

    fatoLayer.querySelectorAll('rect').forEach(rect => {
      rect.setAttribute('fill', hex);
    });
  }

  function _updateSwatches(activeId) {
    document.querySelectorAll('.color-swatch').forEach(el => {
      el.classList.toggle('selected', el.dataset.colorId === activeId);
    });
  }

  return {
    init() {
      const grid = document.getElementById('colorGrid');
      if (!grid) return;

      COLOR_PALETTE.forEach(c => {
        const swatch = document.createElement('div');
        swatch.className = 'color-swatch';
        swatch.dataset.colorId = c.id;
        swatch.style.background = c.hex;
        swatch.title = c.label;
        swatch.onclick = () => this.applyColor(c.id);
        grid.appendChild(swatch);
      });

      // Aplicar cor inicial
      const initial = COLOR_PALETTE[0];
      this.applyColor(initial.id);

      console.log('[FASE 2] ColorFilter iniciado —', COLOR_PALETTE.length, 'cores');
    },

    applyColor(colorId) {
      const c = COLOR_PALETTE.find(p => p.id === colorId);
      if (!c) return;

      AppState.set({ selectedColor: colorId });
      _applyToDOM(c.hex);
      _updateSwatches(colorId);

      console.log('[ColorFilter] Cor aplicada:', colorId, c.hex);
    },
  };

})();

// ----------------------------------------------------------------
// BitmapShader — FASE 2
// Aplica textura de tecido à layer do fato
// ----------------------------------------------------------------
const BitmapShader = (() => {

  function _applyToDOM(fabric) {
    // Prévia web via CSS background-image
    // INTEGRAÇÃO: substituir por:
    //   val textureBitmap = BitmapFactory.decodeStream(assets.open(fabric.texturePath))
    //   val shader = BitmapShader(textureBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
    //   paint.shader = shader
    //   (aplicar sobre a layer fato após ColorFilter)
    const fatoLayer = document.getElementById('layer-fato');
    if (!fatoLayer) return;

    // Em SVG, textura é aproximada — na integração real usa PNG bitmap
    const svg = document.getElementById('mannequinSVG');
    if (!svg) return;

    // Remover pattern anterior
    let defs = svg.querySelector('defs');
    if (!defs) {
      defs = document.createElementNS('http://www.w3.org/2000/svg', 'defs');
      svg.insertBefore(defs, svg.firstChild);
    }
    defs.innerHTML = '';

    if (fabric.id !== 'none') {
      // Criar pattern SVG como aproximação visual da textura
      const pattern = document.createElementNS('http://www.w3.org/2000/svg', 'pattern');
      pattern.setAttribute('id', 'fabricPattern');
      pattern.setAttribute('patternUnits', 'userSpaceOnUse');
      pattern.setAttribute('width', '8');
      pattern.setAttribute('height', '8');

      // Padrão simplificado por tipo de tecido
      const patternData = {
        wool:   '<line x1="0" y1="0" x2="8" y2="8" stroke="rgba(255,255,255,0.06)" stroke-width="1"/>',
        linen:  '<line x1="0" y1="4" x2="8" y2="4" stroke="rgba(255,255,255,0.08)" stroke-width="0.8"/>',
        silk:   '<line x1="8" y1="0" x2="0" y2="8" stroke="rgba(255,255,255,0.05)" stroke-width="0.5"/>',
        tweed:  '<rect x="0" y="0" width="4" height="4" fill="rgba(255,255,255,0.04)"/>',
        velvet: '<circle cx="4" cy="4" r="1.5" fill="rgba(255,255,255,0.06)"/>',
        cotton: '<line x1="4" y1="0" x2="4" y2="8" stroke="rgba(255,255,255,0.07)" stroke-width="0.7"/>',
      };

      pattern.innerHTML = patternData[fabric.id] || '';
      defs.appendChild(pattern);

      fatoLayer.querySelectorAll('rect').forEach(rect => {
        rect.setAttribute('fill', 'url(#fabricPattern)');
      });
    }
  }

  function _updateCards(activeId) {
    document.querySelectorAll('.fabric-card').forEach(el => {
      el.classList.toggle('selected', el.dataset.fabricId === activeId);
    });
  }

  return {
    init() {
      const grid = document.getElementById('fabricGrid');
      if (!grid) return;

      FABRIC_CATALOG.forEach(f => {
        const card = document.createElement('div');
        card.className = 'fabric-card';
        card.dataset.fabricId = f.id;
        card.onclick = () => this.applyFabric(f.id);

        // Prévia de textura (CSS — substituir por img com texturePath na integração)
        const preview = document.createElement('div');
        preview.className = 'fabric-preview';
        preview.style.background = f.previewBg;
        preview.style.backgroundImage = f.previewPattern;
        preview.style.backgroundSize = '8px 8px';

        const label = document.createElement('div');
        label.textContent = f.label;

        card.appendChild(preview);
        card.appendChild(label);
        grid.appendChild(card);
      });

      console.log('[FASE 2] BitmapShader iniciado —', FABRIC_CATALOG.length, 'tecidos');
    },

    applyFabric(fabricId) {
      const f = FABRIC_CATALOG.find(fc => fc.id === fabricId);
      if (!f) return;

      AppState.set({ selectedFabric: fabricId });
      _applyToDOM(f);
      _updateCards(fabricId);

      console.log('[BitmapShader] Tecido aplicado:', fabricId, '→', f.texturePath);
    },
  };

})();

// ----------------------------------------------------------------
// SliderController — FASE 2
// Sliders afetam a layer correta em tempo real (critério da fase)
// ----------------------------------------------------------------
const SliderController = (() => {

  // Mapa de funções de aplicação por slider
  // INTEGRAÇÃO: substituir por animateFloatAsState + Modifier.graphicsLayer{}
  const _appliers = {

    lapela(value) {
      // Ajusta largura da lapela → scaleX do layer-lapela
      // INTEGRAÇÃO: graphicsLayer { scaleX = lerp(0.5f, 1.5f, value / 100f) }
      const layer = document.getElementById('layer-lapela');
      if (!layer) return;
      const scale = 0.5 + (value / 100) * 1.0;
      layer.style.transform = 'scaleX(' + scale.toFixed(3) + ')';
      layer.style.transformOrigin = 'center top';
    },

    comprimento(value) {
      // Ajusta comprimento do fato → scaleY do layer-fato
      // INTEGRAÇÃO: graphicsLayer { scaleY = lerp(0.7f, 1.3f, value / 100f) }
      const layer = document.getElementById('layer-fato');
      if (!layer) return;
      const scale = 0.7 + (value / 100) * 0.6;
      layer.style.transform = 'scaleY(' + scale.toFixed(3) + ')';
      layer.style.transformOrigin = 'center top';
    },

    ombro(value) {
      // Ajusta largura dos ombros → scaleX do layer-fato (eixo horizontal)
      // INTEGRAÇÃO: graphicsLayer { scaleX = lerp(0.85f, 1.25f, value / 100f) }
      const layer = document.getElementById('layer-fato');
      if (!layer) return;
      const scaleX = 0.85 + (value / 100) * 0.4;
      layer.style.transform = 'scaleX(' + scaleX.toFixed(3) + ')';
      layer.style.transformOrigin = 'center top';
    },
  };

  return {
    init() {
      const container = document.getElementById('slidersContainer');
      if (!container) return;

      SLIDER_DEFINITIONS.forEach(s => {
        const row = document.createElement('div');
        row.className = 'slider-row';

        // Label + valor actual
        const labelRow = document.createElement('div');
        labelRow.className = 'slider-label';
        labelRow.innerHTML =
          '<span>' + s.label.toUpperCase() + '</span>' +
          '<span id="sliderVal-' + s.id + '">' + s.value + s.unit + '</span>';

        // Input range
        const slider = document.createElement('input');
        slider.type  = 'range';
        slider.min   = s.min;
        slider.max   = s.max;
        slider.value = s.value;
        slider.step  = 1;
        slider.id    = 'slider-' + s.id;

        slider.oninput = (e) => {
          const v = parseInt(e.target.value, 10);
          AppState.setSlider(s.id, v);

          // Atualizar label
          const valLabel = document.getElementById('sliderVal-' + s.id);
          if (valLabel) valLabel.textContent = v + s.unit;

          // Aplicar à layer correcta em tempo real (critério FASE 2)
          if (_appliers[s.id]) _appliers[s.id](v);

          console.log('[SliderController]', s.id, '=', v, '→ layer:', s.targetLayer);
        };

        row.appendChild(labelRow);
        row.appendChild(slider);
        container.appendChild(row);

        // Aplicar valor inicial
        if (_appliers[s.id]) _appliers[s.id](s.value);
      });

      console.log('[FASE 2] SliderController iniciado —', SLIDER_DEFINITIONS.length, 'sliders');
    },

    // Actualizar slider externamente (ex: restaurar estado)
    setValue(sliderId, value) {
      const el = document.getElementById('slider-' + sliderId);
      if (el) {
        el.value = value;
        const valLabel = document.getElementById('sliderVal-' + sliderId);
        const s = SLIDER_DEFINITIONS.find(d => d.id === sliderId);
        if (valLabel && s) valLabel.textContent = value + s.unit;
      }
      AppState.setSlider(sliderId, value);
      if (_appliers[sliderId]) _appliers[sliderId](value);
    },
  };

})();
