package com.popo2381.coffeeshop.domain.order.dto.response;

public record OrderCreateResponse(
        Long orderId,
        Long userId,
        Long menuId,
        int paidPoint,
        int remainingPoint
) {
}