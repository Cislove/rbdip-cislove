package com.rbdip.bookstore.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreateOrderRequest(
        @NotBlank String customerFullName,
        @NotBlank String customerAddress,
        String customerPhone,
        String customerType,
        String couponCode,
        @NotEmpty List<Item> items) {

    public record Item(
            @NotNull Long productId,
            @Positive Integer quantity) {
    }
}
