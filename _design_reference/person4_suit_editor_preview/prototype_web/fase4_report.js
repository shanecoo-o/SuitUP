/* ================================================================
   fase4_report.js — FASE 4: Relatório editor_3d_report.md
   Suit Editor 3D

   Responsabilidades:
     ✓ Listar layers implementadas com descrição
     ✓ Documentar assets (nome, resolução, fonte)
     ✓ Funcionalidades prontas vs pendentes
     ✓ Métricas de performance (FPS, render, APK size)
     ✓ Gerar ficheiro Markdown exportável

   INTEGRAÇÃO ANDROID:
     Gerar o .md em tempo de CI/CD via script Kotlin/Gradle:
       task generateReport {
         doLast {
           val report = ReportEngine.generate(buildConfig)
           File("editor_3d_report.md").writeText(report.toMarkdown())
         }
       }
     FPS real: medido via Choreographer.FrameCallback no Android
     APK size: obtido via apkanalyzer ou BuildConfig.VERSION_CODE
================================================================ */

'use strict';

const ReportEngine = (() => {

  // ----------------------------------------------------------------
  // Gerar dados do relatório a partir do estado actual
  // ----------------------------------------------------------------
  function _generate() {
    const state = AppState.get();

    const activeLayerCount = Object.values(state.layerVisibility)
      .filter(Boolean).length;

    return {
      // ---- Layers (FASE 1) ----
      layers: LAYER_DEFINITIONS.map(layer => ({
        id:         layer.id,
        label:      layer.label,
        zOrder:     layer.z,
        color:      layer.color,
        visible:    state.layerVisibility[layer.id],
        // INTEGRAÇÃO: resolver resolução real do PNG exportado
        resolution: '200×360px @1x · 400×720px @2x · 600×1080px @3x',
        source:     'assets/' + layer.id + '@2x.png',
        status:     'implementada',
      })),

      // ---- Assets do manequim (FASE 1) ----
      mannequinAssets: ANGLES.map((angle, i) => {
        const key = ['front', 'side', 'back'][i];
        return {
          angle,
          path2x: MANNEQUIN_ASSETS[key]['2x'],
          path3x: MANNEQUIN_ASSETS[key]['3x'],
          status: 'pending — substituir SVG por PNG real',
        };
      }),

      // ---- Funcionalidades (FASE 2 + 3) ----
      features: {
        'ColorFilter (cor base)': {
          status:  'done',
          detail:  'Cor activa: ' + state.selectedColor +
                   ' (' + (COLOR_PALETTE.find(c => c.id === state.selectedColor)?.hex || '?') + ')',
          fase:    2,
        },
        'Paleta ≥8 cores': {
          status:  'done',
          detail:  COLOR_PALETTE.length + ' cores disponíveis',
          fase:    2,
        },
        'BitmapShader (textura)': {
          status:  'done',
          detail:  'Tecido activo: ' + state.selectedFabric,
          fase:    2,
        },
        'Catálogo de tecidos': {
          status:  'done',
          detail:  FABRIC_CATALOG.map(f => f.label).join(', '),
          fase:    2,
        },
        'Slider lapela': {
          status:  'done',
          detail:  'Valor: ' + state.sliders.lapela + '% → layer-lapela scaleX',
          fase:    2,
        },
        'Slider comprimento': {
          status:  'done',
          detail:  'Valor: ' + state.sliders.comprimento + '% → layer-fato scaleY',
          fase:    2,
        },
        'Slider ombro': {
          status:  'done',
          detail:  'Valor: ' + state.sliders.ombro + '% → layer-fato scaleX',
          fase:    2,
        },
        'Sliders em tempo real': {
          status:  'done',
          detail:  'Critério FASE 2 — layer afectada imediatamente no oninput',
          fase:    2,
          criterio: true,
        },
        'Manequim PNG transparente': {
          status:  'pending',
          detail:  'Substituir SVG por PNG real (front/side/back @2x @3x)',
          fase:    3,
        },
        'Swipe 3 ângulos + indicador': {
          status:  'done',
          detail:  ANGLES.join(' · ') + ' — ângulo actual: ' + ANGLES[state.activeAngle],
          fase:    3,
        },
        'Crossfade entre ângulos': {
          status:  'done',
          detail:  ANIM_CONFIG.crossfadeDurationMs + 'ms opacity + scale transition',
          fase:    3,
        },
        'Pinch-to-zoom + FAB': {
          status:  'done',
          detail:  'Zoom ×' + ANIM_CONFIG.zoomScale + ' via FAB e pinch — activo: ×' + state.zoomLevel,
          fase:    3,
        },
        'Preview sincronizado com Editor 2D': {
          status:  'done',
          detail:  'Critério FASE 3 — AppState.subscribe() propaga mudanças em tempo real',
          fase:    3,
          criterio: true,
        },
        'Validação composição (sem bleeding)': {
          status:  state.composition.validated ? 'done' : 'pending',
          detail:  state.composition.validated
                     ? 'Validado: ' + state.composition.lastValidation
                     : 'Pressionar "Validar Composição"',
          fase:    1,
          criterio: false,
        },
      },

      // ---- Performance (FASE 4) ----
      performance: {
        fps:           state.performance.fps,
        renderMs:      state.performance.renderMs,
        apkSizeKb:     state.performance.apkKb,
        layersActive:  activeLayerCount + '/' + LAYER_DEFINITIONS.length,
        // INTEGRAÇÃO: medir via:
        //   FPS → Choreographer.FrameCallback doFrame()
        //   renderMs → trace { ... } com Systrace / Perfetto
        //   APK size → ./gradlew :app:bundleRelease + apkanalyzer
      },

      // ---- Meta ----
      generated:   new Date().toISOString(),
      version:     '1.0.0',
      phases:      { fase1: 'Layers & Assets', fase2: 'Customização',
                     fase3: 'Preview 3D Fake', fase4: 'Relatório' },
    };
  }

  // ----------------------------------------------------------------
  // Renderizar relatório no modal HTML
  // ----------------------------------------------------------------
  function _renderHTML(report) {
    const container = document.getElementById('reportContent');
    if (!container) return;
    container.innerHTML = '';

    // ---- Secção: Layers ----
    const s1 = _makeSection('Layers implementadas (' +
      report.layers.filter(l => l.visible).length + '/' +
      report.layers.length + ' activas)');

    report.layers.forEach(l => {
      const row = _makeRow(
        'Z' + l.zOrder + ' · ' + l.label,
        (l.visible ? '<span class="badge-ok">● activo</span>' : '○ oculto')
      );
      s1.appendChild(row);
      // Linha de asset
      const assetRow = _makeRow(
        '&nbsp;&nbsp;&nbsp;' + l.source,
        l.resolution,
        true
      );
      s1.appendChild(assetRow);
    });
    container.appendChild(s1);

    // ---- Secção: Funcionalidades ----
    const s2 = _makeSection('Funcionalidades');
    Object.entries(report.features).forEach(([name, f]) => {
      const label = name + (f.criterio ? ' ★' : '');
      const badge = f.status === 'done'
        ? '<span class="badge-ok">✓ done</span>'
        : '<span class="badge-pending">○ pending</span>';
      const row = _makeRow('[F' + f.fase + '] ' + label, badge);
      row.title = f.detail;
      row.style.cursor = 'help';
      s2.appendChild(row);
      // Detalhe em linha muted
      const detail = document.createElement('div');
      detail.className = 'report-row';
      detail.innerHTML =
        '<span style="color:var(--color-text-muted);padding-left:12px;font-size:10px;">' +
        f.detail + '</span>';
      s2.appendChild(detail);
    });
    container.appendChild(s2);

    // ---- Secção: Assets do manequim ----
    const s3 = _makeSection('Assets manequim (FASE 1 → FASE 3)');
    report.mannequinAssets.forEach(a => {
      const row = _makeRow(
        a.angle,
        '<span class="badge-pending">pending PNG</span>'
      );
      s3.appendChild(row);
      const pathRow = _makeRow(
        '&nbsp;&nbsp;' + a.path2x + ' · ' + a.path3x,
        '',
        true
      );
      s3.appendChild(pathRow);
    });
    container.appendChild(s3);

    // ---- Secção: Performance ----
    const s4 = _makeSection('Métricas de performance');
    [
      ['FPS target',     report.performance.fps + ' fps'],
      ['Render time',    report.performance.renderMs + ' ms'],
      ['APK size',       report.performance.apkSizeKb + ' KB (pending build)'],
      ['Layers activas', report.performance.layersActive],
    ].forEach(([k, v]) => s4.appendChild(_makeRow(k, v)));
    container.appendChild(s4);

    // ---- Footer ----
    const ts = document.createElement('div');
    ts.style.cssText =
      'font-size:10px;color:var(--color-text-muted);margin-top:16px;' +
      'letter-spacing:0.06em;border-top:0.5px solid var(--color-border);' +
      'padding-top:10px;';
    ts.innerHTML =
      'GERADO: ' + report.generated + '<br>' +
      'VERSÃO: ' + report.version + ' &nbsp;|&nbsp; ★ = critério de fase';
    container.appendChild(ts);

    // Botão de download do Markdown
    const dlBtn = document.createElement('button');
    dlBtn.className = 'close-btn';
    dlBtn.style.marginRight = '10px';
    dlBtn.textContent = 'DOWNLOAD .MD';
    dlBtn.onclick = () => _downloadMarkdown(report);
    container.appendChild(dlBtn);
  }

  function _makeSection(title) {
    const s = document.createElement('div');
    s.className = 'report-section';
    s.innerHTML = '<h3>' + title + '</h3>';
    return s;
  }

  function _makeRow(key, valHTML, muted = false) {
    const row = document.createElement('div');
    row.className = 'report-row';
    row.innerHTML =
      '<span class="report-key"' +
        (muted ? ' style="font-size:10px;opacity:0.6;"' : '') +
        '>' + key + '</span>' +
      '<span class="report-val">' + valHTML + '</span>';
    return row;
  }

  // ----------------------------------------------------------------
  // Serializar relatório para Markdown
  // ----------------------------------------------------------------
  function _toMarkdown(report) {
    const lines = [];
    lines.push('# editor_3d_report.md');
    lines.push('');
    lines.push('> Gerado: ' + report.generated + '  ');
    lines.push('> Versão: ' + report.version);
    lines.push('');

    lines.push('## Layers implementadas');
    lines.push('');
    lines.push('| Z | Layer | Asset | Resolução | Status |');
    lines.push('|---|-------|-------|-----------|--------|');
    report.layers.forEach(l => {
      lines.push(
        '| Z' + l.zOrder + ' | ' + l.label + ' | `' + l.source + '` | ' +
        l.resolution + ' | ' + (l.visible ? '✓ activo' : '○ oculto') + ' |'
      );
    });
    lines.push('');

    lines.push('## Assets do Manequim');
    lines.push('');
    report.mannequinAssets.forEach(a => {
      lines.push('- **' + a.angle + '**: `' + a.path2x + '` · `' + a.path3x + '` — ' + a.status);
    });
    lines.push('');

    lines.push('## Funcionalidades');
    lines.push('');
    lines.push('| Fase | Feature | Status | Detalhe |');
    lines.push('|------|---------|--------|---------|');
    Object.entries(report.features).forEach(([name, f]) => {
      lines.push(
        '| F' + f.fase + ' | ' + name + (f.criterio ? ' ★' : '') +
        ' | ' + (f.status === 'done' ? '✓' : '○') +
        ' | ' + f.detail + ' |'
      );
    });
    lines.push('');
    lines.push('★ = critério de fase');
    lines.push('');

    lines.push('## Métricas de Performance');
    lines.push('');
    lines.push('| Métrica | Valor |');
    lines.push('|---------|-------|');
    lines.push('| FPS target | ' + report.performance.fps + ' fps |');
    lines.push('| Render time | ' + report.performance.renderMs + ' ms |');
    lines.push('| APK size | ' + report.performance.apkSizeKb + ' KB (pending build) |');
    lines.push('| Layers activas | ' + report.performance.layersActive + ' |');
    lines.push('');

    lines.push('---');
    lines.push('*Ordem obrigatória: Fase 1 → 2 → 3 → 4*  ');
    lines.push('*Fases 2 e 3 dependem dos assets da Fase 1*  ');
    lines.push('*Relatório só após implementação concluída*');

    return lines.join('\n');
  }

  // ----------------------------------------------------------------
  // Download automático do ficheiro .md
  // ----------------------------------------------------------------
  function _downloadMarkdown(report) {
    const md = _toMarkdown(report);
    const blob = new Blob([md], { type: 'text/markdown;charset=utf-8' });
    const url  = URL.createObjectURL(blob);
    const a    = document.createElement('a');
    a.href     = url;
    a.download = 'editor_3d_report.md';
    a.click();
    URL.revokeObjectURL(url);
  }

  // ----------------------------------------------------------------
  // API pública
  // ----------------------------------------------------------------
  return {
    init() {
      console.log('[FASE 4] ReportEngine pronto');
    },

    // Abrir modal com relatório gerado
    open() {
      const report = _generate();
      _renderHTML(report);
      document.getElementById('reportOverlay').classList.add('open');
      console.log('[FASE 4] Relatório gerado:', report.generated);
    },

    close() {
      document.getElementById('reportOverlay').classList.remove('open');
    },

    // Retornar dados do relatório como objeto (para integração)
    generate:     _generate,
    toMarkdown:  (r) => _toMarkdown(r || _generate()),
    download:    ()  => _downloadMarkdown(_generate()),
  };

})();
