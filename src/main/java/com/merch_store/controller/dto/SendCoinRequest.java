package com.merch_store.controller.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public record SendCoinRequest(
        @NotNull(message = "поле toUser является обязательным")
        String toUser,
        @NotNull(message = "поле amount является обязательным")
        Long amount
) { }
