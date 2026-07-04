package com.suitup.backend.common;

import java.math.BigDecimal;

public final class MoneyValidator {

    public static final String DEFAULT_CURRENCY = "MZN";

    private MoneyValidator() {
    }

    public static BigDecimal requireNonNegative(BigDecimal amount, String fieldName) {
        if (amount == null || amount.signum() < 0) {
            throw new BadRequestException(fieldName + " deve ser maior ou igual a zero");
        }
        return amount;
    }

    public static String normalizeCurrency(String currency) {
        String normalized = currency == null || currency.isBlank()
            ? DEFAULT_CURRENCY
            : currency.trim().toUpperCase();
        if (!DEFAULT_CURRENCY.equals(normalized)) {
            throw new BadRequestException("A moeda suportada nesta fase é MZN");
        }
        return normalized;
    }
}
