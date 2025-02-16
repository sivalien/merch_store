package com.merch_store.repository.dto;


public record UserInventory(
    String username,
    String inventoryType,
    Integer quantity
) {}
