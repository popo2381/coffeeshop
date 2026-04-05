package com.popo2381.coffeeshop.domain.menu.dto.response;

public record PopularMenuResponse(
        Long menuId,
        String name,
        int price,
        long orderCount
) {
}