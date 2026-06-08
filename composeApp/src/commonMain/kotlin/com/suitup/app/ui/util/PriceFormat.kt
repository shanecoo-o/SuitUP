package com.suitup.app.ui.util

/**
 * Formatação de preços em Metical moçambicano (MT).
 *
 * Convenção interna: valores no domain são Int em **meticais inteiros**
 * (não em centavos — Moçambique não usa subdivisão prática).
 * Ex: 3450 → "3.450,00 MT"
 *
 * Para uso futuro com centavos basta mudar a impl interna.
 */
object PriceFormat {

    /**
     * Formata um valor em meticais como "X.XXX,00 MT".
     * Usa ponto como separador de milhares e vírgula como decimal (padrão PT).
     */
    fun mt(amountMt: Int): String {
        val withThousands = amountMt.toString()
            .reversed()
            .chunked(3)
            .joinToString(".")
            .reversed()
        return "$withThousands,00 MT"
    }

    /**
     * Variante para Double (caso precise de centavos no futuro).
     */
    fun mt(amountMt: Double): String {
        val integerPart = amountMt.toInt()
        val cents = ((amountMt - integerPart) * 100).toInt()
        val withThousands = integerPart.toString()
            .reversed()
            .chunked(3)
            .joinToString(".")
            .reversed()
        val centsStr = cents.toString().padStart(2, '0')
        return "$withThousands,$centsStr MT"
    }
}
