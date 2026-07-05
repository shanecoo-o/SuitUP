package com.suitup.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/**
 * Image container foundation (Task 19). Fixes aspect ratio, background, shape
 * and content scale per usage context so callers never pick these values ad hoc.
 */
enum class SuitImageContext { ProductCard, ProductDetail, EditorStage, Thumbnail }

private data class ImageContainerSpec(
    val aspectRatio: Float?,
    val contentScale: ContentScale,
    val shape: RoundedCornerShape,
    val innerPadding: androidx.compose.ui.unit.Dp,
)

@Composable
private fun specFor(context: SuitImageContext): ImageContainerSpec = when (context) {
    SuitImageContext.ProductCard -> ImageContainerSpec(0.82f, ContentScale.Fit, SuitTheme.shapes.md, 8.dp)
    SuitImageContext.ProductDetail -> ImageContainerSpec(1f, ContentScale.Fit, SuitTheme.shapes.lg, 16.dp)
    SuitImageContext.EditorStage -> ImageContainerSpec(1f, ContentScale.Fit, SuitTheme.shapes.md, 0.dp)
    SuitImageContext.Thumbnail -> ImageContainerSpec(1f, ContentScale.Crop, SuitTheme.shapes.sm, 0.dp)
}

@Composable
fun SuitImageContainer(
    image: DrawableResource,
    contentDescription: String?,
    context: SuitImageContext,
    modifier: Modifier = Modifier,
) {
    val spec = specFor(context)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .let { if (spec.aspectRatio != null) it.aspectRatio(spec.aspectRatio) else it.fillMaxSize() }
            .clip(spec.shape)
            .background(SuitColors.WarmBlack),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .padding(spec.innerPadding),
            contentScale = spec.contentScale,
        )
    }
}
