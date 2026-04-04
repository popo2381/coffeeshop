package com.popo2381.coffeeshop.domain.point.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PointChargeRequest(
        @NotNull Long userId,
        @NotNull @Min(1) Integer amount
) {
}