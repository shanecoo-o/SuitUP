# editor_preview_report.md — Relatório Técnico do Editor SuitUP

**Módulo:** Editor 2D + Preview Pseudo-3D
**Versão:** 1.0.0
**Data:** Junho 2024
**Autor:** Pessoa 4 — SuitUP Team

---

## 1. Introdução

Este relatório documenta o módulo de personalização de fatos do app SuitUP. O módulo foi desenvolvido como protótipo técnico em HTML/CSS/JavaScript e serve como referência de lógica para conversão para Kotlin Multiplatform + Compose Multiplatform.

O protótipo demonstra a arquitectura completa do editor, incluindo gestão de layers, personalização de cor e tecido, sliders de ajuste visual e preview pseudo-3D em 3 ângulos.

---

## 2. Objectivo do Módulo Editor/Preview

O módulo tem como objectivo principal permitir ao utilizador personalizar visualmente o seu fato antes de o adicionar ao carrinho.

Funcionalidades cobertas:
- Visualização do fato em tempo real durante a personalização
- Selecção de cor (16 opções) via ColorFilter
- Selecção de tecido (4 opções) via BitmapShader
- Ajuste visual de lapela, comprimento e ombro via sliders
- Visualização em 3 ângulos: frente, lado, costas
- Geração da configuração final para o carrinho/checkout

---

## 3. Como Funciona o Layer Manager

O Layer Manager (FASE 1) é responsável por organizar e renderizar as partes do fato como camadas sobrepostas.

Cada layer representa uma parte do fato (casaco base, lapela, botões, bolsos, calças, gola) e tem um Z-order que define a ordem de renderização: Z:0 é desenhado primeiro (base), Z:4 por último (topo).

O utilizador pode ligar e desligar a visibilidade de layers individuais. As layers marcadas como `toggleable: false` são sempre visíveis e não podem ser escondidas.

A validação de composição verifica se todas as layers activas estão correctamente presentes no canvas, sem sobreposição indevida (bleeding) entre camadas.

Referência Kotlin: `EditorLayer.kt.txt`, `compose_conversion_plan.md → FASE 1`

---

## 4. Como Funciona a Personalização por Cor

A personalização de cor usa o conceito de ColorFilter.

No protótipo web, a cor é aplicada como atributo `fill` SVG directamente sobre os elementos dos layers.

No app Android/Compose, deve ser implementado como:

```kotlin
ColorFilter.tint(selectedColor.composeColor, BlendMode.SrcIn)
```

Aplicado como modificador nas layers que têm `canChangeColor: true`. A cor afecta todas as layers do fato de forma coerente, mantendo a textura do tecido por cima.

Referência Kotlin: `SuitColorOption.kt.txt`

---

## 5. Como Funciona a Personalização por Tecido

A personalização de tecido usa o conceito de BitmapShader.

No protótipo web, a textura é simulada via CSS `background-image` com `repeating-linear-gradient`.

No app Android/Compose, deve ser implementado como:

```kotlin
val shader = BitmapShader(textureBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
paint.shader = shader
```

Ou em Compose usando `ImageShader` com `TileMode.Repeated`.

O catálogo contém 4 tecidos (wool, cotton, linen, premium_black). Cada tecido tem um modificador de preço que ajusta o `precoTotal` na configuração final.

Referência Kotlin: `FabricOption.kt.txt`

---

## 6. Como Funcionam os Sliders

Os sliders de ajuste visual (FASE 2) afectam as layers correctas em tempo real.

Existem 3 sliders:

**Lapela (0–100%):** Afecta a layer `lapel` (frente). Varia o `scaleX` entre 0.5 e 1.5. O efeito visual é a largura da lapela a aumentar ou diminuir.

**Comprimento (0–100%):** Afecta as layers `jacket_base` e `sleeve` (todos os ângulos). Varia o `scaleY` entre 0.7 e 1.3. O efeito visual é o comprimento do casaco e das mangas.

**Ombro (0–100%):** Afecta a layer `jacket_base` (todos os ângulos). Varia o `scaleX` entre 0.85 e 1.25. O efeito visual é a largura dos ombros.

Em Compose, a animação deve usar `animateFloatAsState` com `tween(100ms)` para suavidade.

Referência Kotlin: `EditorSlider.kt.txt`

---

## 7. Como Funciona o Preview Pseudo-3D

**Importante:** O preview **não é 3D real**. É uma visualização simulada do fato em 3 ângulos estáticos.

Arquitectura do preview (FASE 3):

O canvas central mostra o manequim como fundo (PNG com fundo transparente) e as layers do fato compostas por cima. O utilizador pode navegar entre os 3 ângulos (frente, lado, costas) via swipe horizontal ou botões de navegação.

A transição entre ângulos usa crossfade (fade out em 125ms, troca de ângulo, fade in em 125ms), com total de 250ms.

O zoom é activado via FAB ou pinch-to-zoom, com factor máximo de 1.6x.

O preview é sincronizado com o editor em tempo real: qualquer mudança de cor, tecido ou slider reflecte imediatamente no canvas.

Em Compose, implementar com `HorizontalPager` + `AnimatedContent` com `fadeIn/fadeOut(tween(250))`.

Referência Kotlin: `PreviewAngle.kt.txt`, `compose_conversion_plan.md → FASE 3`

---

## 8. Como o Editor Gera a Configuração Final do Fato

Quando o utilizador confirma a personalização, o editor chama `buildFinalConfiguration()` que gera um objecto `FinalSuitConfiguration` com todos os dados necessários.

Este objecto contém: modelo, cor, tecido, valores dos sliders, detalhes do fato (lapela, botões, bolsos, fit), preço total e flag `necessitaMedidas: true`.

Ver exemplo completo em: `editor_data/final_suit_configuration_example.json`

---

## 9. Como a Configuração Passa para Carrinho/Checkout

O fluxo é:

1. Editor confirma → `buildFinalConfiguration()` → `FinalSuitConfiguration`
2. `CartViewModel.addItem(cartItem)` → item adicionado ao carrinho
3. Utilizador confirma carrinho → redireccionado para Medidas
4. Medidas preenchidas → Checkout / Pagamento
5. Pagamento confirmado → Tracking criado

Ver detalhes completos em: `integration_notes/checkout_integration.md`

---

## 10. Limitações Actuais

- O preview é pseudo-3D (3 ângulos estáticos), não motor 3D real
- O código do protótipo (HTML/JS) não entra directamente no KMP
- Todos os assets em `assets/` são placeholders — o designer deve criar os PNGs finais
- Sem integração de backend ainda
- Sliders sem animação Compose real (a implementar com `animateFloatAsState`)
- Validação de composição superficial no protótipo

Ver detalhes em: `integration_notes/limitations.md`

---

## 11. Próximas Melhorias

Prioridade imediata:
1. Designer cria PNGs reais dos layers (frente, lado, costas) com fundo transparente
2. Conversão da lógica para Kotlin/Compose seguindo `compose_conversion_plan.md`
3. Integração com `CartViewModel` da outra pessoa da equipa
4. Implementar `animateFloatAsState` nos sliders

Melhorias futuras:
- Mais ângulos (ex: 45°, 135°)
- Pré-visualização em tempo real do tecido sobre o fato (blend real)
- Salvar configurações para retomar mais tarde
- Modo comparação (2 configurações lado a lado)

---

## 12. Conclusão

O módulo de editor e preview pseudo-3D está arquitecturalmente bem definido e pronto para conversão para Kotlin/Compose. A lógica de layers, estado, customização e geração da configuração final está documentada e mapeada para os equivalentes Kotlin nos ficheiros `kotlin_mapping/`.

O próximo passo crítico é o designer criar os assets PNG finais e o Codex usar este pacote como guia técnico para implementar o módulo no projecto KMP oficial.

O código HTML/JS do protótipo não deve ser reutilizado directamente — serve exclusivamente como referência visual e de lógica.
