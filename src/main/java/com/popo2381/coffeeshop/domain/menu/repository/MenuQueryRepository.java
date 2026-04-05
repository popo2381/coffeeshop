package com.popo2381.coffeeshop.domain.menu.repository;

import com.popo2381.coffeeshop.domain.menu.dto.response.PopularMenuResponse;

import java.util.List;

public interface MenuQueryRepository {

    List<PopularMenuResponse> findTop3PopularMenusByLast7Days();
}