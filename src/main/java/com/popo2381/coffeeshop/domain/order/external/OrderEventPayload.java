package com.popo2381.coffeeshop.domain.order.external;

public record OrderEventPayload(
        Long userId,
        Long menuId,
        int price
) {
}