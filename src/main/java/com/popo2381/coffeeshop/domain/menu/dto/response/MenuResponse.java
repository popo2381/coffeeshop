package com.popo2381.coffeeshop.domain.menu.dto.response;

import com.popo2381.coffeeshop.domain.menu.entity.Menu;
import lombok.Getter;

@Getter
public class MenuResponse {

    private final Long menuId;
    private final String name;
    private final int price;

    public MenuResponse(Menu menu) {
        this.menuId = menu.getId();
        this.name = menu.getName();
        this.price = menu.getPrice();
    }
}