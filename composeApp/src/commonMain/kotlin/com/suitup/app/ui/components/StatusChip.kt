package com.suitup.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class StatusChipType(val label: String, val kind: SuitStatusKind) {
    Pending("Pendente", SuitStatusKind.Pendente),
    Confirmed("Confirmado", SuitStatusKind.Success),
    Rejected("Rejeitado", SuitStatusKind.Error),
    Production("Em produção", SuitStatusKind.Info),
    Delivered("Entregue", SuitStatusKind.Success),
    Active("Activo", SuitStatusKind.Success),
    Inactive("Inactivo", SuitStatusKind.Neutral),
    Analysis("Em análise", SuitStatusKind.Info),
    Ready("Pronto", SuitStatusKind.Success),
}

@Composable
fun StatusChip(
    status: StatusChipType,
    modifier: Modifier = Modifier,
    label: String = status.label,
) {
    SuitStatusBadge(text = label, kind = status.kind, modifier = modifier)
}
