package com.popo2381.coffeeshop.domain.menu.controller;

import com.popo2381.coffeeshop.domain.menu.dto.response.MenuResponse;
import com.popo2381.coffeeshop.domain.menu.dto.response.PopularMenuResponse;
import com.popo2381.coffeeshop.domain.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/menus")
public class MenuController {

    private final MenuService menuService;

    // 메뉴 단건 조회
    @GetMapping
    public List<MenuResponse> getMenus() {
        return menuService.getMenus();
    }

    // 최근 7일 인기 메뉴 Top3 조회
    @GetMapping("/popular")
    public ResponseEntity<List<PopularMenuResponse>> getPopularMenus() {
        return ResponseEntity.ok(menuService.getPopularMenus());
    }
}