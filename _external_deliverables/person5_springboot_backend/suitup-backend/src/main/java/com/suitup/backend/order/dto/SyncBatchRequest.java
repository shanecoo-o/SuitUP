package com.suitup.backend.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * REQUISITO DE ENTRADA DO LOTE (Store and Forward Sync Batch)
 * Contém a lista de pedidos coletados off-line na app móvel KMP.
 */
public class SyncBatchRequest {

    @NotEmpty(message = "A lista de pedidos para sincronização não pode estar vazia")
    @Valid
    private List<CreateOrderRequest> orders;

    public SyncBatchRequest() {}

    public SyncBatchRequest(List<CreateOrderRequest> orders) {
        this.orders = orders;
    }

    public List<CreateOrderRequest> getOrders() { return orders; }
    public void setOrders(List<CreateOrderRequest> orders) { this.orders = orders; }
}
