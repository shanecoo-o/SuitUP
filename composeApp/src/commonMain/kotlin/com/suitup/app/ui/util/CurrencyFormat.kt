package com.suitup.app.ui.util

/**
 * Formata valor em meticais (MZN) no padrão moçambicano: "MZN 3.450,00".
 *
 * Convenção: o valor de entrada é em **meticais inteiros** (não centavos).
 * Para 3450 meticais → "MZN 3.450,00"
 * Para 150.5 meticais → "MZN 150,50"
 *
 * Quando integrarmos com backend, mover para domain/money.
 */
fun formatMetical(amount: Double): String {
    val intPart = amount.toLong()
    val decimals = ((amount - intPart) * 100).toLong().let {
        if (it < 0) -it else it
    }

    // Adicionar separador de milhares (.)
    val intStr = intPart.toString()
    val withSeparator = buildString {
        val s = intStr.removePrefix("-")
        var count = 0
        for (i in s.indices.reversed()) {
            append(s[i])
            count++
            if (count % 3 == 0 && i != 0) append('.')
        }
        if (intPart < 0) append('-')
    }.reversed()

    val decStr = decimals.toString().padStart(2, '0')

    return "MZN $withSeparator,$decStr"
}

/** Versão inteira (sem decimais quando 0). */
fun formatMetical(amount: Int): String = formatMetical(amount.toDouble())

fun formatMzn(amount: Int): String = formatMetical(amount)

fun formatMzn(amount: Double): String = formatMetical(amount)
