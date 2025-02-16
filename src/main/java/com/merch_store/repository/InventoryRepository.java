package com.merch_store.repository;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class InventoryRepository {
    final private Map<String, Integer> inventoryToPrice = Map.of(
            "t-shirt", 80,
            "cup", 20,
            "book", 50,
            "pen", 10,
            "powerbank", 200,
            "hoody", 300,
            "umbrella", 200,
            "socks", 10,
            "wallet", 50,
            "pink-hoody", 500
    );

    public boolean hasMerge(String merch) {
        return inventoryToPrice.containsKey(merch);
    }

    public Integer findPrice(String inventory) {
        return inventoryToPrice.getOrDefault(inventory, null);
    }
}
