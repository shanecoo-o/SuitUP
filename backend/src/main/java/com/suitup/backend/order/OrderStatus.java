package com.suitup.backend.order;

public enum OrderStatus {
    RECEIVED,
    IN_ANALYSIS,
    MEASUREMENTS_CONFIRMED,
    IN_PRODUCTION,
    READY_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}
