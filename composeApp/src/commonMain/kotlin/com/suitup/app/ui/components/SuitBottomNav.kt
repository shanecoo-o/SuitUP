package com.suitup.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.icons.BagIcon
import com.suitup.app.ui.icons.HomeIcon
import com.suitup.app.ui.icons.PersonIcon
import com.suitup.app.ui.icons.ShirtIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme

enum class SuitTab(val label: String) {
    Home("Início"),
    Models("Modelos"),
    Orders("Pedidos"),
    Profile("Perfil")
}

@Composable
fun SuitBottomNav(
    selected: SuitTab,
    onSelect: (SuitTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().background(SuitColors.Black)) {
        HorizontalDivider(thickness = 1.dp, color = SuitColors.Mist)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            SuitTab.entries.forEach { tab ->
                NavItem(
                    tab = tab,
                    isSelected = selected == tab,
                    onClick = { onSelect(tab) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    tab: SuitTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tint = if (isSelected) SuitColors.Gold else SuitColors.Smoke
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(modifier = Modifier.padding(horizontal = 4.dp)) {
            when (tab) {
                SuitTab.Home -> HomeIcon(tint = tint, filled = isSelected)
                SuitTab.Models -> ShirtIcon(tint = tint, filled = isSelected)
                SuitTab.Orders -> BagIcon(tint = tint, filled = isSelected)
                SuitTab.Profile -> PersonIcon(tint = tint, filled = isSelected)
            }
        }
        Text(
            text = tab.label,
            style = SuitTextStyles.labelSmall,
            color = tint,
        )
    }
}
