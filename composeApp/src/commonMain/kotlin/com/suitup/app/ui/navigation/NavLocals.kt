package com.suitup.app.ui.navigation

import androidx.compose.runtime.compositionLocalOf

/**
 * Callback de logout global, fornecida pelo Navigator outer (o que envolve o MainShellScreen).
 *
 * O ProfileVoyagerScreen consome isto para fazer logout que destrói o MainShell
 * e volta ao AuthFlow. Sem este local, o ProfileVoyagerScreen está dentro de um
 * Navigator interno (da ProfileTab) que não tem acesso direto ao stack outer.
 *
 * Default: no-op. MainShellScreen sobrescreve com o callback real.
 */
val LocalSignOut = compositionLocalOf<() -> Unit> { { } }
