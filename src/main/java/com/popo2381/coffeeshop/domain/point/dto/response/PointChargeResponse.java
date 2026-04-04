package com.popo2381.coffeeshop.domain.point.dto.response;

public record PointChargeResponse(
        Long userId,
        int chargedAmount,
        int balance
) {
}