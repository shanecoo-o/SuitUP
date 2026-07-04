package com.suitup.data

import com.suitup.model.*

object MockData {
    val suits = listOf(
        Suit("1", "The Executive", "Lã virgem super 120s, corte slim fit clássico.", 1200.0, "https://example.com/suit1.png", "Business"),
        Suit("2", "The Wedding", "Seda e lã, lapela de cetim, elegância suprema.", 1500.0, "https://example.com/suit2.png", "Ceremony"),
        Suit("3", "The Casual", "Linho premium, desestruturado para conforto.", 800.0, "https://example.com/suit3.png", "Casual")
    )

    val fabrics = listOf(
        Fabric("f1", "Royal Wool", "Wool", "#2A2A2A", ""),
        Fabric("f2", "Midnight Silk", "Silk Blend", "#121212", ""),
        Fabric("f3", "Classic Tweed", "Tweed", "#4A4A4A", "")
    )

    val recentOrders = listOf(
        Order("ORD-001", "The Executive", "Em Produção", "20 Out 2023", 1200.0),
        Order("ORD-002", "The Casual", "Entregue", "15 Set 2023", 800.0)
    )
}
