# layers_table.md — Tabela de Layers do Editor SuitUP

> Referência técnica para conversão para Kotlin/Compose.
> Cada linha corresponde a uma layer do fato, com todos os atributos necessários para implementação.

## Tabela completa

| Layer | Ângulo | Z-Order | Customizável | Tipo Customização | Visibilidade Togglável | Asset | Observações |
|---|---|---:|---|---|---|---|---|
| trousers | front | 0 | Sim | cor / tecido | Não | assets/suit_layers/front/trousers.png | Base das calças, sempre visível |
| jacket_base | front | 1 | Sim | cor / tecido | Não | assets/suit_layers/front/jacket_base.png | Silhueta principal do casaco |
| lapel | front | 2 | Sim | cor / tecido | Sim | assets/suit_layers/front/lapel.png | Slider de ajuste de largura |
| buttons | front | 3 | Sim | cor | Sim | assets/suit_layers/front/buttons.png | Afectado por slider de botões |
| pockets | front | 4 | Sim | cor / tecido | Sim | assets/suit_layers/front/pockets.png | Bolso de lapela + bolsos laterais |
| trousers | side | 0 | Sim | cor / tecido | Não | assets/suit_layers/side/trousers.png | Vista lateral das calças |
| jacket_base | side | 1 | Sim | cor / tecido | Não | assets/suit_layers/side/jacket_base.png | Vista lateral do casaco |
| sleeve | side | 2 | Sim | cor / tecido | Sim | assets/suit_layers/side/sleeve.png | Manga — afectada por slider comprimento |
| trousers | back | 0 | Sim | cor / tecido | Não | assets/suit_layers/back/trousers.png | Vista traseira das calças |
| jacket_base | back | 1 | Sim | cor / tecido | Não | assets/suit_layers/back/jacket_base.png | Vista traseira do casaco |
| collar | back | 2 | Sim | cor / tecido | Sim | assets/suit_layers/back/collar.png | Gola traseira |

## Regras de Z-Order

```
Z:0 → base (trousers) — desenhado primeiro
Z:1 → casaco base — sobre as calças
Z:2 → detalhes principais (lapela, manga, collar)
Z:3 → acessórios (botões)
Z:4 → sobreposições finais (bolsos)
```

Implementação em Compose:
```kotlin
// Ordenar por zOrder ascendente e desenhar por cima
val sortedLayers = layers.sortedBy { it.zOrder }
sortedLayers.forEach { layer ->
    if (layer.visible) {
        Image(
            painter = painterResource(layer.assetResId),
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    colorFilter = BlendModeColorFilter(layer.currentColor, BlendMode.SrcIn)
                }
        )
    }
}
```

## Correspondência de assets por ângulo

```
Ângulo FRONT  →  mannequin_front_placeholder.png  +  suit_layers/front/*.png
Ângulo SIDE   →  mannequin_side_placeholder.png   +  suit_layers/side/*.png
Ângulo BACK   →  mannequin_back_placeholder.png   +  suit_layers/back/*.png
```

## Layers afectadas por cada slider

| Slider | Layer(s) afectada(s) | Propriedade | Compose Modifier |
|---|---|---|---|
| lapela | lapel (front) | scaleX | `graphicsLayer { scaleX = v }` |
| comprimento | jacket_base, sleeve (all angles) | scaleY | `graphicsLayer { scaleY = v }` |
| ombro | jacket_base (all angles) | scaleX | `graphicsLayer { scaleX = v }` |

## Resolução recomendada dos assets finais

| Resolução | Uso |
|---|---|
| 200×360 @1x | Preview pequeno / thumbnail |
| 400×720 @2x | Display padrão (maioria dos ecrãs) |
| 600×1080 @3x | Ecrãs de alta densidade (XXHDPI) |

> Em Android, colocar os PNGs em: `res/drawable-mdpi/`, `res/drawable-xhdpi/`, `res/drawable-xxhdpi/`
