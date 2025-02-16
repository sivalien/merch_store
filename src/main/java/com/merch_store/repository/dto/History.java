package com.merch_store.repository.dto;

public record History(
    Long id,
    String fromUser,
    String toUser,
    Long amount
) {}
