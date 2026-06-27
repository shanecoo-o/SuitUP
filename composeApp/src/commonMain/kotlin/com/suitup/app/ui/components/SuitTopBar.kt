package com.suitup.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.icons.BackChevronIcon
import com.suitup.app.ui.icons.CartIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme

/**
 * Top bar SuitUP.
 *
 * Princípios:
 * - Background transparente por defeito (usa Bone do screen)
 * - Variante Dark para ecrãs como o "Acompanhar Pedido"
 * - Cart badge dourado (acento)
 */
@Composable
fun SuitTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    onBack: (() -> Unit)? = null,
    onCart: (() -> Unit)? = null,
    cartBadgeCount: Int = 0,
    dark: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
    centerContent: (@Composable () -> Unit)? = null,
) {
    val bg = if (dark) SuitColors.Black else Color.Transparent
    val fg = SuitColors.Ink

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Leading
        Box(modifier = Modifier.size(40.dp)) {
            if (onBack != null) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    BackChevronIcon(tint = fg)
                }
            }
        }

        // Center: prefer centerContent slot over title text
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when {
                centerContent != null -> centerContent()
                title != null -> Text(
                    text = title,
                    style = SuitTextStyles.titleLarge,
                    color = fg,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // Trailing
        Row(
            modifier = Modifier.widthIn(min = 40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
        ) {
            trailing?.invoke()
            if (onCart != null) {
                Box(
                    modifier = Modifier.size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { onCart() },
                        contentAlignment = Alignment.Center
                    ) {
                        CartIcon(tint = fg)
                    }
                    if (cartBadgeCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(SuitColors.Gold),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cartBadgeCount.toString(),
                                style = SuitTextStyles.labelSmall,
                                color = SuitColors.GoldInk
                            )
                        }
                    }
                }
            }
        }
    }
}
