package com.suitup.app.ui.util

import org.jetbrains.compose.resources.DrawableResource
import suitup.composeapp.generated.resources.Res
import suitup.composeapp.generated.resources.suit_casual_linen
import suitup.composeapp.generated.resources.suit_classic_black
import suitup.composeapp.generated.resources.suit_grey_slim
import suitup.composeapp.generated.resources.suit_navy_business

fun suitImageResourceOrNull(key: String): DrawableResource? = when (key) {
    "suit_classic_black" -> Res.drawable.suit_classic_black
    "suit_grey_slim" -> Res.drawable.suit_grey_slim
    "suit_navy_business" -> Res.drawable.suit_navy_business
    "suit_casual_linen" -> Res.drawable.suit_casual_linen
    else -> null
}

fun suitImageResource(key: String): DrawableResource =
    suitImageResourceOrNull(key) ?: Res.drawable.suit_classic_black
