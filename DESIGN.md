# SuitUP · Design System

Este documento codifica os tokens, princípios e regras visuais do SuitUP.
É a fonte da verdade para qualquer ecrã ou componente novo.

## 1. Identidade

SuitUP é uma app moçambicana de personalização de fatos sob medida com visualização 2D/3D, pagamento M-Pesa e entrega via Maputo. A linguagem visual é **alta alfaiataria minimalista** — preto refinado, off-whites cálidos, dourado escasso como acento, tipografia editorial. O oposto de "SaaS startup glow".

**Register**: produto (não brand/marketing). O design serve a tarefa.

## 2. Color strategy

Estratégia: **Restrained** — neutros cálidos + um único acento dourado usado em ≤10% da superfície.

| Token | Hex | Uso |
|---|---|---|
| `Ink` | `#0D0D0D` | Texto principal, botões primários, linhas de força. Nunca `#000000` puro. |
| `Charcoal` | `#2C2C2C` | Texto secundário forte, top bars escuras. |
| `Bone` | `#F5F5F5` | Fundo principal de ecrã. |
| `Pearl` | `#FAFAFA` | Surface alternativa, backgrounds de inputs em estados disabled. |
| `Gold` | `#C8A96A` | Acento. CTA destacado, badges, eyebrow opcional. **≤10% da superfície.** |
| `Mist` | `#EAEAEA` | Bordas hairline, dividers. |
| `Slate` | `#787774` | Texto secundário, helpers, eyebrows. |
| `Smoke` | `#B8B8B8` | Texto desativado, placeholders. |

Pastéis para status (muito desaturados):
- Verde `#EDF3EC` / `#346538` · Sucesso, "Entregue"
- Âmbar `#FBF3DB` / `#956400` · Pendente, "Em produção", "Aguardando"
- Vermelho `#FDEBEC` / `#9F2F2D` · Erro, "Cancelado"
- Azul `#E1F3FE` / `#1F6C9F` · Info, "Enviado"

## 3. Typography

| Style | Font | Size | Weight | Uso |
|---|---|---|---|---|
| Display L | Playfair Display | 48/52 | Bold | Hero "Feito à sua medida" |
| Display M | Playfair Display | 36/40 | Bold | Onboarding hero |
| Headline L | Playfair Display | 24/30 | SemiBold | Títulos de ecrã principais |
| Headline M | Playfair Display | 20/26 | SemiBold | Títulos de cards/secções |
| Title L | Inter | 16/22 | SemiBold | Top bar title, button labels grandes |
| Body L | Inter | 16/26 | Regular | Texto longo, descrições |
| Body M | Inter | 14/22 | Regular | Texto padrão da UI |
| Body S | Inter | 12/18 | Regular | Helper text, captions |
| Eyebrow | Inter | 10/14 | SemiBold | Tracking 1.6sp, UPPERCASE — antes de títulos importantes |
| Mono | JetBrains Mono | 13/18 | Regular | Números M-Pesa, códigos de pedido |

Regras:
- `line-height` body = 1.6 (legibilidade)
- Headlines usam `letter-spacing` negativo (-0.5 a -1.2sp) para densidade visual
- **Nunca** usar Serif para dashboards/listas de dados. Apenas para hero/títulos editoriais.

## 4. Spacing

Escala em 4dp. Tokens semânticos:
- `screenPadding` = 24dp
- `cardPadding` = 20dp
- `cardGap` = 12dp
- `sectionGap` = 32dp
- `fieldGap` = 16dp (entre inputs)
- `inputGap` = 8dp (entre label e input)

Princípio: **vary spacing for rhythm**. Não usar o mesmo padding em tudo.

## 5. Shapes

- Buttons, inputs, cards: 8-12dp (crisp, não pill)
- Tags, badges, avatars, status dots: pill (999dp)
- Bottom sheets: 20dp top corners
- **Nunca** usar `RoundedCornerShape(999.dp)` em containers grandes (cards, hero areas)

## 6. Borders & shadows

- Border padrão: `1dp solid #EAEAEA`
- Border focused: `1dp solid #0D0D0D`
- Border error: `1dp solid #9F2F2D`
- **Sem shadows pesadas.** Hierarquia por borders + spacing + cor de fundo (Bone vs SurfaceWhite).
- Se uma shadow for indispensável, usar `0 2px 8px rgba(0,0,0,0.04)` máximo.

## 7. Motion

- Durations: 80 (instant), 150 (fast), 250 (normal), 400 (slow), 600 (deliberate), 800 (cinematic)
- Easing default: ease-out exponencial `cubic-bezier(0.16, 1, 0.3, 1)`
- **Sem bounce, sem elastic.**
- Tactile feedback em botões: `scale(0.98)` em :active
- Scroll-entry: fade + translateY 12dp em 600ms
- Stagger em listas: 80ms entre items

## 8. Componentes core

Todos prefixados `Suit*`:
- `SuitButton` (Primary/Secondary/Ghost/Gold, 3 tamanhos)
- `SuitTextField` (label acima, helper/error abaixo)
- `SuitTopBar` (back + title + cart, light/dark)
- `SuitBottomNav` (4 tabs: Início · Modelos · Pedidos · Perfil)
- `SuitStepIndicator` (1-2-3-4 numerado)
- `SuitCard` (border 1dp, background branco)
- `SuitEyebrow` (uppercase tracking largo)
- `SuitStatusBadge` (pill com pastel + ink)
- `SuitLogoMark` / `SuitLogoStack` / `SuitLogoInline`

## 9. Banidos (AI-slop test)

Se alguém olhar para um ecrã e pensar "AI fez isto", falhámos. **Nunca**:

- `#000000` puro · usar `#0D0D0D`
- `#FFFFFF` em toda parte · usar `#F5F5F5` Bone como base
- Gradient text (background-clip text) em headers
- Cards aninhados (card dentro de card) — sempre wrong
- Side-stripe borders (`borderLeft` colorido como acento)
- Glassmorphism decorativo
- Em-dashes (`—`) em UI copy. Usar vírgula, dois pontos ou ponto.
- Generic names: "John Doe", "Acme Corp" · usar nomes moçambicanos realistas (João da Silva, Tiago Macuácua, Paula Mondlane)
- Números falsos perfeitos: `99.99%`, `R$ 1.000,00` · usar valores orgânicos (R$ 3.450, R$ 150 entrega)
- Inter/Roboto exclusivos · Inter é OK como body mas o caráter vem do Playfair em headlines
- Bullets de "elevate", "seamless", "next-gen" no copy
- 3-column card grids genéricos

## 10. Specifics moçambicanos

- Moeda: `R$` (Metical) — formato `R$ 3.450,00`
- Telefone: `+258 84 123 4567`
- Cidades: Maputo (default), Matola, Beira, Nampula
- Bairros Maputo: Polana, Sommerschield, Alto Maé, Costa do Sol, Malhangalene
- Pagamento: M-Pesa Manual + comprovativo (upload imagem)
- Entrega: Delivery (motoboy) ou Levantamento (loja)
