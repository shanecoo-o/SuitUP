# person4_suit_editor_preview — SuitUP Editor Module

**Responsável:** Pessoa 4
**Módulo:** Editor 2D + Preview Pseudo-3D
**Estado:** Protótipo técnico — pronto para conversão para Kotlin/Compose

---

## O que está nesta pasta

```
person4_suit_editor_preview/
├── README.md                         ← Este ficheiro
├── editor_preview_report.md          ← Relatório técnico completo (12 secções)
│
├── prototype_web/                    ← Protótipo HTML/JS (referência de lógica)
│   ├── index.html
│   ├── styles.css
│   ├── config.js
│   ├── state.js
│   ├── fase1_layer_manager.js
│   ├── fase2_customization.js
│   ├── fase3_preview.js
│   ├── fase4_report.js
│   └── app.js
│
├── assets/                           ← PNGs placeholder organizados
│   ├── mannequin/                    ← Manequim por ângulo (frente/lado/costas)
│   ├── suit_layers/
│   │   ├── front/                    ← 5 layers (jacket_base, lapel, buttons, pockets, trousers)
│   │   ├── side/                     ← 3 layers (jacket_base, sleeve, trousers)
│   │   └── back/                     ← 3 layers (jacket_base, collar, trousers)
│   └── fabrics/                      ← 4 texturas (wool, cotton, linen, premium_black)
│
├── layer_mapping/
│   └── layers_table.md              ← Tabela completa de layers com Z-order e atributos
│
├── editor_data/
│   ├── editor_config.json           ← Configuração estática do editor
│   ├── editor_state_example.json    ← Estado durante edição
│   └── final_suit_configuration_example.json ← Payload para carrinho/checkout
│
├── kotlin_mapping/
│   ├── EditorState.kt.txt           ← EditorState + EditorViewModel
│   ├── EditorLayer.kt.txt           ← EditorLayer + SuitLayerStack composable
│   ├── FabricOption.kt.txt          ← FabricOption + BitmapShader
│   ├── SuitColorOption.kt.txt       ← SuitColorOption + ColorFilter
│   ├── PreviewAngle.kt.txt          ← PreviewAngle enum + AnglePreviewPager
│   └── EditorSlider.kt.txt          ← EditorSlider + SuitSlider composable
│
└── integration_notes/
    ├── compose_conversion_plan.md   ← Mapa JS → Kotlin/Compose com exemplos de código
    ├── checkout_integration.md      ← Como o editor se liga ao carrinho/checkout
    └── limitations.md               ← Limitações actuais documentadas
```

---

## Para o Codex / próxima pessoa

**O protótipo web NÃO entra directamente no projecto KMP.**
Usa os ficheiros nesta ordem:

1. `layer_mapping/layers_table.md` — perceber como as layers funcionam
2. `editor_data/editor_config.json` — dados estáticos do editor
3. `kotlin_mapping/*.kt.txt` — data classes e composables prontos
4. `integration_notes/compose_conversion_plan.md` — como converter cada módulo
5. `integration_notes/checkout_integration.md` — como ligar ao carrinho
6. `editor_preview_report.md` — relatório técnico completo

---

## Assets que o designer precisa criar

Substituir os placeholders em `assets/` por PNGs reais:
- Fundo transparente (RGBA)
- Resolução: 200×360 @1x · 400×720 @2x · 600×1080 @3x
- Um ficheiro por layer, por ângulo

---

## Limitações importantes

- Preview é **pseudo-3D** (3 ângulos estáticos), não motor 3D real
- Código HTML/JS é referência, não implementação final
- Assets são placeholders — designer deve criar versões finais
- Ver `integration_notes/limitations.md` para detalhes completos
