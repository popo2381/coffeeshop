package com.popo2381.coffeeshop.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;

public record OrderCreateRequest(
        @NotNull Long userId,
        @NotNull Long menuId
) {
}