package com.popo2381.coffeeshop.domain.menu.service;

import com.popo2381.coffeeshop.domain.menu.dto.response.MenuResponse;
import com.popo2381.coffeeshop.domain.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    public List<MenuResponse> getMenus() {
        return menuRepository.findAll()
                .stream()
                .map(MenuResponse::new)
                .toList();
    }
}