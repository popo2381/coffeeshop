package com.popo2381.coffeeshop.domain.menu.service;

import com.popo2381.coffeeshop.domain.menu.dto.response.MenuResponse;
import com.popo2381.coffeeshop.domain.menu.dto.response.PopularMenuResponse;
import com.popo2381.coffeeshop.domain.menu.repository.MenuQueryRepository;
import com.popo2381.coffeeshop.domain.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuQueryRepository menuQueryRepository;

    public List<MenuResponse> getMenus() {
        return menuRepository.findAll()
                .stream()
                .map(MenuResponse::new)
                .toList();
    }

    public List<PopularMenuResponse> getPopularMenus() {
        // 최근 7일간 주문 수 기준 인기 메뉴 조회
        return menuQueryRepository.findTop3PopularMenusByLast7Days();
    }
}