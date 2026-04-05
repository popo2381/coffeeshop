package com.popo2381.coffeeshop.domain.menu.service;

import com.popo2381.coffeeshop.domain.menu.dto.response.MenuResponse;
import com.popo2381.coffeeshop.domain.menu.dto.response.PopularMenuResponse;
import com.popo2381.coffeeshop.domain.menu.repository.MenuQueryRepository;
import com.popo2381.coffeeshop.domain.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuQueryRepository menuQueryRepository;

    // 단건 메뉴 조회
    @Transactional(readOnly = true)
    public List<MenuResponse> getMenus() {
        return menuRepository.findAll()
                .stream()
                .map(MenuResponse::new)
                .toList();
    }

    // 최근 7일 인기 메뉴 Top3 조회
    @Transactional(readOnly = true)
    public List<PopularMenuResponse> getPopularMenus() {
        return menuQueryRepository.findTop3PopularMenusByLast7Days();
    }
}