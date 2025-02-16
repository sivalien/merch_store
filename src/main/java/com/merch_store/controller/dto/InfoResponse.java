package com.merch_store.controller.dto;

import java.util.List;

public record InfoResponse(Long coins,
                           List<InventoryEntry> inventory,
                           CoinHistoryResponse coinHistory) {
}
