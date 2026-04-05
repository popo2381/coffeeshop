package com.popo2381.coffeeshop.domain.order.dto.response;

import com.popo2381.coffeeshop.domain.order.entity.Order;
import com.popo2381.coffeeshop.domain.order.entity.OrderStatus;

import java.time.LocalDateTime;

public record OrderSummaryResponse(
        Long orderId,
        Long menuId,
        String menuName,
        int price,
        OrderStatus status,
        LocalDateTime orderedAt
) {
    public static OrderSummaryResponse from(Order order) {
        return new OrderSummaryResponse(
                order.getId(),
                order.getMenu().getId(),
                order.getMenu().getName(),
                order.getPrice(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}