package com.suitup.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.icons.CloseIcon
import com.suitup.app.ui.icons.PictureIcon
import com.suitup.app.ui.icons.UploadIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme

/**
 * Card de upload com border dashed.
 *
 * Estado vazio: ícone upload + título "Selecionar imagem" + hint de formatos.
 * Estado preenchido: nome do ficheiro + tamanho + ícone close para remover.
 *
 * O Compose Multiplatform não tem dashed border nativa, por isso desenhamos
 * manualmente via `drawBehind` + `dashPathEffect`.
 */
@Composable
fun SuitUploadCard(
    uploadedFileName: String?,
    onPickFile: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Enviar comprovativo",
    hint: String = "PNG, JPG ou PDF até 10MB",
    onRemove: () -> Unit = {},
) {
    val cornerRadius = 12.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(if (uploadedFileName == null) 120.dp else 72.dp)
            .clip(SuitTheme.shapes.lg)
            .background(SuitColors.WarmBlack)
            .dashedBorder(
                color = SuitColors.Mist,
                cornerRadius = cornerRadius,
                strokeWidth = 1.dp.value,
                dashLength = 6f,
                gapLength = 4f,
            )
            .clickable(enabled = uploadedFileName == null, onClick = onPickFile),
        contentAlignment = Alignment.Center,
    ) {
        if (uploadedFileName == null) {
            EmptyState(title = title, hint = hint)
        } else {
            FilledState(
                fileName = uploadedFileName,
                onRemove = onRemove,
            )
        }
    }
}

@Composable
private fun EmptyState(title: String, hint: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(16.dp),
    ) {
        UploadIcon(tint = SuitColors.Gold, size = 22.dp)
        Spacer(Modifier.height(2.dp))
        Text(
            text = title,
            style = SuitTextStyles.titleSmall,
            color = SuitColors.Ink,
        )
        Text(
            text = hint,
            style = SuitTextStyles.bodySmall,
            color = SuitColors.Slate,
        )
    }
}

@Composable
private fun FilledState(fileName: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(28.dp),
            contentAlignment = Alignment.Center
        ) {
            PictureIcon(tint = SuitColors.Gold, size = 22.dp)
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = fileName,
                style = SuitTextStyles.titleSmall,
                color = SuitColors.Ink,
            )
            Text(
                text = "Toca em ✕ para remover",
                style = SuitTextStyles.bodySmall,
                color = SuitColors.Slate,
            )
        }
        Box(
            modifier = Modifier
                .size(28.dp)
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center
        ) {
            CloseIcon(tint = SuitColors.Slate, size = 18.dp)
        }
    }
}

/**
 * Modifier extension para desenhar border dashed.
 */
private fun Modifier.dashedBorder(
    color: Color,
    cornerRadius: androidx.compose.ui.unit.Dp,
    strokeWidth: Float,
    dashLength: Float,
    gapLength: Float,
): Modifier = this.drawBehind {
    val stroke = Stroke(
        width = strokeWidth,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, gapLength), 0f)
    )
    val cornerPx = cornerRadius.toPx()
    drawRoundRect(
        color = color,
        topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
        size = Size(size.width - strokeWidth, size.height - strokeWidth),
        cornerRadius = CornerRadius(cornerPx, cornerPx),
        style = stroke
    )
}
