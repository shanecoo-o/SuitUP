package com.suitup.app.ui.screens.editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.CorFato
import com.suitup.app.domain.model.EstiloBotao
import com.suitup.app.domain.model.EstiloForro
import com.suitup.app.domain.model.EstiloManga
import com.suitup.app.domain.model.PartesFato
import com.suitup.app.domain.model.Tecido
import com.suitup.app.domain.model.TipoBolso
import com.suitup.app.domain.model.TipoLapela
import com.suitup.app.ui.components.ColorSwatch
import com.suitup.app.ui.components.EditorHotspotDot
import com.suitup.app.ui.components.EditorHotspotVisualState
import com.suitup.app.ui.components.EditorOptionCard
import com.suitup.app.ui.components.FabricSwatch
import com.suitup.app.ui.components.PrimaryGoldButton
import com.suitup.app.ui.components.SuitImmersiveScaffold
import com.suitup.app.ui.components.SuitImmersiveTopBar
import com.suitup.app.ui.components.SuitSlider
import com.suitup.app.ui.icons.CartIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme
import com.suitup.app.ui.util.formatMzn
import com.suitup.app.ui.util.suitImageResource
import com.suitup.app.ui.util.toComposeColorOrNull
import org.jetbrains.compose.resources.painterResource

/**
 * Real Editor 2D experience (Phase 9.5A) — an immersive full-bleed stage over the actual
 * selected-model product photo, with tappable hotspots opening a bottom customization
 * sheet, replacing the old two-step parts/colors screens.
 */
@Composable
fun Editor2DStageScreen(
    imageKey: String,
    modelName: String,
    basePriceMzn: Int,
    partes: PartesFato,
    corFato: CorFato,
    tecido: Tecido,
    coresFato: List<CorFato>,
    tecidos: List<Tecido>,
    vestIncluded: Boolean,
    tieStyle: TieStyle,
    cartItemCount: Int,
    onBack: () -> Unit,
    onCartClick: () -> Unit,
    onLapelChange: (TipoLapela) -> Unit,
    onColorSelect: (CorFato) -> Unit,
    onFabricSelect: (Tecido) -> Unit,
    onButtonsChange: (EstiloBotao) -> Unit,
    onPocketChange: (TipoBolso) -> Unit,
    onSleevesChange: (EstiloManga) -> Unit,
    onLiningChange: (EstiloForro) -> Unit,
    onFitChange: (Float) -> Unit,
    onVestToggle: (Boolean) -> Unit,
    onTieStyleChange: (TieStyle) -> Unit,
    onNext: () -> Unit,
) {
    var activeCategory by remember { mutableStateOf<EditorHotspotCategory?>(null) }

    SuitImmersiveScaffold(
        topBar = {
            SuitImmersiveTopBar(
                onBack = onBack,
                title = modelName,
                trailing = { EditorCartTrailing(cartItemCount = cartItemCount, onCartClick = onCartClick) },
            )
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                EditorStage(
                    imageKey = imageKey,
                    corFato = corFato,
                    activeCategory = activeCategory,
                    onHotspotClick = { category -> activeCategory = category },
                )
                EditorCtaBar(
                    modelName = modelName,
                    basePriceMzn = basePriceMzn,
                    onNext = onNext,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
                if (activeCategory != null) {
                    val category = activeCategory!!
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(SuitColors.Scrim)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { activeCategory = null },
                    )
                    EditorCustomizationSheet(
                        selectedCategory = category,
                        onCategorySelect = { activeCategory = it },
                        onClose = { activeCategory = null },
                        modifier = Modifier.align(Alignment.BottomCenter),
                    ) {
                        CategoryBody(
                            category = category,
                            partes = partes,
                            tecido = tecido,
                            corFato = corFato,
                            coresFato = coresFato,
                            tecidos = tecidos,
                            vestIncluded = vestIncluded,
                            tieStyle = tieStyle,
                            onLapelChange = onLapelChange,
                            onColorSelect = onColorSelect,
                            onFabricSelect = onFabricSelect,
                            onButtonsChange = onButtonsChange,
                            onPocketChange = onPocketChange,
                            onSleevesChange = onSleevesChange,
                            onLiningChange = onLiningChange,
                            onFitChange = onFitChange,
                            onVestToggle = onVestToggle,
                            onTieStyleChange = onTieStyleChange,
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun EditorCartTrailing(cartItemCount: Int, onCartClick: () -> Unit) {
    Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable(onClick = onCartClick),
            contentAlignment = Alignment.Center,
        ) {
            CartIcon(tint = SuitColors.Pearl)
        }
        if (cartItemCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(SuitColors.Gold),
                contentAlignment = Alignment.Center,
            ) {
                Text(cartItemCount.toString(), style = SuitTextStyles.labelSmall, color = SuitColors.GoldInk)
            }
        }
    }
}

/**
 * The stage image + hotspot overlay. Hotspot coordinates are resolved against
 * [resolveFitRect] (the actual letterboxed image rectangle), never the outer stage box,
 * so dots stay pinned to the garment regardless of aspect ratio or device size.
 */
@Composable
private fun EditorStage(
    imageKey: String,
    corFato: CorFato,
    activeCategory: EditorHotspotCategory?,
    onHotspotClick: (EditorHotspotCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val painter = painterResource(suitImageResource(imageKey))
        val fitRect = remember(maxWidth, maxHeight, painter.intrinsicSize) {
            resolveFitRect(
                stageWidth = maxWidth.value,
                stageHeight = maxHeight.value,
                sourceWidth = painter.intrinsicSize.width,
                sourceHeight = painter.intrinsicSize.height,
            )
        }
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
        )
        EditorHotspotLayout.stageHotspots.forEach { hotspot ->
            val centerX = fitRect.left + hotspot.normalizedX * fitRect.width
            val centerY = fitRect.top + hotspot.normalizedY * fitRect.height
            EditorHotspotDot(
                label = hotspot.category.label,
                state = if (activeCategory == hotspot.category) {
                    EditorHotspotVisualState.Active
                } else {
                    EditorHotspotVisualState.Default
                },
                onClick = { onHotspotClick(hotspot.category) },
                modifier = Modifier.offset(x = (centerX - 22f).dp, y = (centerY - 22f).dp),
            )
        }
        ColorBadge(
            corFato = corFato,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 64.dp, end = 16.dp),
        )
    }
}

/**
 * Controlled-overlay classification (Task 8, category B) for the "Color" hotspot: a
 * floating badge reflecting the selected color, not a full-photo tint — there is no
 * per-garment alpha mask to isolate just the jacket, so tinting the whole photo would
 * also recolor skin/shirt/background.
 */
@Composable
private fun ColorBadge(corFato: CorFato, modifier: Modifier = Modifier) {
    val color = remember(corFato.hex) { corFato.hex.toComposeColorOrNull() ?: SuitColors.Ink }
    Row(
        modifier = modifier
            .clip(SuitTheme.shapes.pill)
            .background(SuitColors.WarmBlack.copy(alpha = 0.7f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(color)
                .border(1.dp, SuitColors.Pearl, CircleShape),
        )
        Text(corFato.nome, style = SuitTextStyles.labelSmall, color = SuitColors.Pearl)
    }
}

@Composable
private fun EditorCtaBar(
    modelName: String,
    basePriceMzn: Int,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(SuitColors.WarmBlack.copy(alpha = 0f), SuitColors.WarmBlack)))
            .padding(horizontal = SuitTheme.responsive.horizontalContentPadding, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(modelName.ifBlank { "Modelo seleccionado" }, style = SuitTextStyles.titleMedium, color = SuitColors.Pearl)
            Text(formatMzn(basePriceMzn), style = SuitTextStyles.titleMedium, color = SuitColors.GoldChampagne)
        }
        PrimaryGoldButton(text = "Ver Preview 3D", onClick = onNext)
    }
}

@Composable
private fun CategoryBody(
    category: EditorHotspotCategory,
    partes: PartesFato,
    corFato: CorFato,
    tecido: Tecido,
    coresFato: List<CorFato>,
    tecidos: List<Tecido>,
    vestIncluded: Boolean,
    tieStyle: TieStyle,
    onLapelChange: (TipoLapela) -> Unit,
    onColorSelect: (CorFato) -> Unit,
    onFabricSelect: (Tecido) -> Unit,
    onButtonsChange: (EstiloBotao) -> Unit,
    onPocketChange: (TipoBolso) -> Unit,
    onSleevesChange: (EstiloManga) -> Unit,
    onLiningChange: (EstiloForro) -> Unit,
    onFitChange: (Float) -> Unit,
    onVestToggle: (Boolean) -> Unit,
    onTieStyleChange: (TieStyle) -> Unit,
) {
    when (category) {
        EditorHotspotCategory.Lapel -> LapelCategoryBody(partes.lapela, onLapelChange)
        EditorHotspotCategory.Fabric -> FabricCategoryBody(tecidos, tecido, onFabricSelect)
        EditorHotspotCategory.Color -> ColorCategoryBody(coresFato, corFato, onColorSelect)
        EditorHotspotCategory.Buttons -> OptionGrid(EstiloBotao.entries, partes.botoes, { it.label }, onButtonsChange)
        EditorHotspotCategory.ChestPocket, EditorHotspotCategory.SidePockets ->
            OptionGrid(TipoBolso.entries, partes.bolso, { it.label() }, onPocketChange)
        EditorHotspotCategory.Sleeves -> OptionGrid(EstiloManga.entries, partes.mangas, { it.label() }, onSleevesChange)
        EditorHotspotCategory.Vest -> VestCategoryBody(vestIncluded, onVestToggle)
        EditorHotspotCategory.Tie -> OptionGrid(TieStyle.entries, tieStyle, { it.label }, onTieStyleChange)
        EditorHotspotCategory.Lining -> OptionGrid(EstiloForro.entries, partes.forro, { it.label }, onLiningChange)
        EditorHotspotCategory.Fit -> FitCategoryBody(partes.ajusteLargura, onFitChange)
    }
}

@Composable
private fun LapelCategoryBody(selected: TipoLapela, onSelect: (TipoLapela) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Escolha o corte da lapela", style = SuitTextStyles.bodySmall, color = SuitColors.Smoke)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            TipoLapela.entries.forEach { type ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    LapelPreviewCard(type = type, selected = type == selected, onClick = { onSelect(type) }, size = 68.dp)
                    Text(
                        type.label,
                        style = SuitTextStyles.labelSmall,
                        color = if (type == selected) SuitColors.GoldChampagne else SuitColors.Smoke,
                    )
                }
            }
        }
    }
}

@Composable
private fun FabricCategoryBody(tecidos: List<Tecido>, selected: Tecido, onSelect: (Tecido) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Escolha o tecido", style = SuitTextStyles.bodySmall, color = SuitColors.Smoke)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(tecidos, key = { it.id }) { fabric ->
                FabricSwatch(
                    color = fabric.hexAmostra.toComposeColorOrNull() ?: SuitColors.Charcoal,
                    label = fabric.nome,
                    selected = fabric.id == selected.id,
                    onClick = { onSelect(fabric) },
                )
            }
        }
    }
}

@Composable
private fun ColorCategoryBody(cores: List<CorFato>, selected: CorFato, onSelect: (CorFato) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Escolha a cor", style = SuitTextStyles.bodySmall, color = SuitColors.Smoke)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(cores, key = { it.id }) { cor ->
                ColorSwatch(
                    color = cor.hex.toComposeColorOrNull() ?: SuitColors.Ink,
                    selected = cor.id == selected.id,
                    label = cor.nome,
                    onClick = { onSelect(cor) },
                )
            }
        }
    }
}

@Composable
private fun VestCategoryBody(included: Boolean, onToggle: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            "Preferência guardada apenas nesta sessão de edição — sem alteração visual no fato.",
            style = SuitTextStyles.bodySmall,
            color = SuitColors.Smoke,
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            EditorOptionCard(
                title = "Sem colete",
                selected = !included,
                onClick = { onToggle(false) },
                modifier = Modifier.weight(1f),
            )
            EditorOptionCard(
                title = "Com colete",
                selected = included,
                onClick = { onToggle(true) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun FitCategoryBody(width: Float, onChange: (Float) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Caimento", style = SuitTextStyles.labelMedium, color = SuitColors.Slate)
            Text(
                fitLabel(width),
                style = SuitTextStyles.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = SuitColors.GoldChampagne,
            )
        }
        SuitSlider(value = width, onValueChange = onChange)
    }
}

internal fun fitLabel(value: Float): String = when {
    value < 0.34f -> "Ajustado"
    value < 0.67f -> "Regular"
    else -> "Solto"
}

@Composable
private fun <T> OptionGrid(options: List<T>, selected: T, label: (T) -> String, onSelect: (T) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        options.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { option ->
                    EditorOptionCard(
                        title = label(option),
                        selected = option == selected,
                        onClick = { onSelect(option) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

/**
 * [TipoBolso] has no domain label — supplied locally since Models.kt is out of scope this
 * phase. `internal` so [Preview3DScreenModel] (same package) can reuse it for the
 * configuration summary instead of duplicating the mapping.
 */
internal fun TipoBolso.label(): String = when (this) {
    TipoBolso.Aba -> "Com aba"
    TipoBolso.Aplicado -> "Aplicado"
    TipoBolso.Embutido -> "Embutido"
}

/** [EstiloManga] has no domain label — supplied locally since Models.kt is out of scope this phase. */
internal fun EstiloManga.label(): String = when (this) {
    EstiloManga.Padrao -> "Padrão"
    EstiloManga.Funcional -> "Funcional"
    EstiloManga.Cirurgiao -> "Botões de cirurgião"
}
