package com.popo2381.coffeeshop.domain.menu.repository;

import com.popo2381.coffeeshop.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}