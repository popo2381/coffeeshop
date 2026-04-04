package com.popo2381.coffeeshop.domain.menu.controller;

import com.popo2381.coffeeshop.domain.menu.dto.MenuResponse;
import com.popo2381.coffeeshop.domain.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/menus")
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public List<MenuResponse> getMenus() {
        return menuService.getMenus();
    }
}