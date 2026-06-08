package com.suitup.app.ui.screens.editor

/**
 * Peças do fato disponíveis para edição.
 * UI-only — representa o estado de navegação dentro do editor 2D, não é domain.
 *
 * O domain PartesFato contém todos os valores; este enum diz qual peça
 * o utilizador está a editar agora.
 */
enum class EditorPart(val label: String) {
    Modelo("Modelo"),
    Gola("Gola"),
    Lapela("Lapela"),
    Bolso("Bolso"),
    Botoes("Botões"),
    Mangas("Mangas"),
    Forro("Forro"),
    Costas("Costas");

    companion object {
        fun all() = entries.toList()
    }
}
