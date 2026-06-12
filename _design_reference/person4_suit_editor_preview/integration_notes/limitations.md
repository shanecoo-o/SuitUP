# limitations.md — Limitações Actuais do Editor SuitUP

## 1. Preview é Pseudo-3D, não 3D Real

O preview do editor **não é um motor 3D real**.

O que existe actualmente:
- 3 imagens estáticas do manequim (frente, lado, costas)
- Layers PNG compostas por cima em Z-order
- Transição entre ângulos via crossfade (opacidade)
- Zoom via transform/scale

O que **não existe** e **não deve ser prometido**:
- Motor de renderização 3D (Three.js, Unity, OpenGL ES, Vulkan)
- Modelo 3D real (OBJ, FBX, GLTF)
- Rotação contínua do manequim
- Iluminação dinâmica 3D
- Física de tecido

**Nome correcto a usar:**
- Preview pseudo-3D
- Preview em 3 ângulos
- Visualização simulada do fato

---

## 2. Protótipo Web não entra directamente no KMP

O código em `prototype_web/` está em **HTML/CSS/JavaScript** e serve apenas como **referência de lógica** e **protótipo visual**.

Não deve ser:
- Copiado directamente para o projecto KMP
- Usado como WebView dentro do app Android
- Referenciado como código de produção

Deve ser:
- Lido pelo Codex como guia de lógica
- Convertido para Kotlin/Compose seguindo o `compose_conversion_plan.md`
- Descartado após conversão

---

## 3. Assets são Placeholders

Todos os PNGs em `assets/` são **placeholders gerados programaticamente**.

Não são:
- Arte final do fato
- Imagens fotorrealistas
- Assets aprovados pelo designer

São apenas:
- Referência de organização de ficheiros
- Guia de nomenclatura e resolução
- Estrutura para o designer substituir pelos assets reais

Assets finais que faltam (a criar pelo designer/equipa):
```
assets/mannequin/mannequin_front_placeholder.png  → PNG real do manequim frente (fundo transparente)
assets/mannequin/mannequin_side_placeholder.png   → PNG real do manequim lado
assets/mannequin/mannequin_back_placeholder.png   → PNG real do manequim costas
assets/suit_layers/front/jacket_base.png          → PNG real do casaco frente (fundo transparente)
assets/suit_layers/front/lapel.png                → PNG real da lapela
... (todos os outros layers)
assets/fabrics/wool.png                           → Textura real de lã (128×128 ou maior)
... (outros tecidos)
```

Resolução necessária: `@1x (200×360)`, `@2x (400×720)`, `@3x (600×1080)` por layer, fundo transparente.

---

## 4. Sem Integração de Backend Ainda

O editor gera um `FinalSuitConfiguration` (JSON) mas ainda não o envia para nenhum backend.

O que falta:
- Endpoint da API para receber a configuração
- Integração com o carrinho da outra pessoa da equipa
- Autenticação do utilizador no editor
- Persistência de configurações (SharedPreferences / servidor)

---

## 5. Sliders sem Animação Compose Real

Os sliders no protótipo web usam CSS transforms para simular o efeito nos layers.

No app Compose final, deve-se usar:
- `animateFloatAsState` para transições suaves
- `graphicsLayer { scaleX = ...; scaleY = ... }` para transforms
- `Spring` ou `Tween` animation spec para sensação responsiva

A lógica de mapeamento (valor 0-100 → escala 0.5-1.5) está definida em `EditorSlider.kt.txt`.

---

## 6. Validação de Composição é Superficial

A função `validateComposition()` no protótipo verifica apenas se os elementos DOM existem.

No app real, deve-se implementar:
- Verificação de pixel overlap entre layers (se aplicável)
- Verificação de assets missing
- Validação de configuração completa antes de enviar ao carrinho

---

## Resumo das limitações

| Limitação | Impacto | Como resolver |
|---|---|---|
| Preview pseudo-3D | Visual apenas | Aceitar como feature, não como bug |
| HTML/JS não entra no KMP | Código não reutilizável | Seguir compose_conversion_plan.md |
| Assets são placeholders | Preview incompleto | Designer cria PNGs finais transparentes |
| Sem backend | Não persiste | Outra pessoa da equipa integra API |
| Sliders sem animação Compose | UX menos fluida | Usar animateFloatAsState no Compose |
| Validação superficial | Risco de composição incorrecta | Implementar pixel-diff real |
