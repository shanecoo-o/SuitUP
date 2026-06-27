package com.suitup.backend.order.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * RELATÓRIO DO LOTE DE ACORDO COM REGRAS DE NEGÓCIO
 * Devolve de maneira sumarizada quais IDs provisórios foram criados
 * com sucesso no PostgreSQL e quais foram ignorados para impedir duplicações.
 */
public class SyncBatchResponse {
    private String status = "COMPLETED";
    private int totalProcessed;
    private int totalSynced;
    private int totalIgnored;
    private List<String> syncedIds = new ArrayList<>();
    private List<String> ignoredIds = new ArrayList<>();

    public SyncBatchResponse() {}

    public SyncBatchResponse(int totalProcessed, int totalSynced, int totalIgnored, 
                             List<String> syncedIds, List<String> ignoredIds) {
        this.totalProcessed = totalProcessed;
        this.totalSynced = totalSynced;
        this.totalIgnored = totalIgnored;
        this.syncedIds = syncedIds;
        this.ignoredIds = ignoredIds;
    }

    // --- GETTERS E SETTERS ---
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getTotalProcessed() { return totalProcessed; }
    public void setTotalProcessed(int totalProcessed) { this.totalProcessed = totalProcessed; }

    public int getTotalSynced() { return totalSynced; }
    public void setTotalSynced(int totalSynced) { this.totalSynced = totalSynced; }

    public int getTotalIgnored() { return totalIgnored; }
    public void setTotalIgnored(int totalIgnored) { this.totalIgnored = totalIgnored; }

    public List<String> getSyncedIds() { return syncedIds; }
    public void setSyncedIds(List<String> syncedIds) { this.syncedIds = syncedIds; }

    public List<String> getIgnoredIds() { return ignoredIds; }
    public void setIgnoredIds(List<String> ignoredIds) { this.ignoredIds = ignoredIds; }
}
