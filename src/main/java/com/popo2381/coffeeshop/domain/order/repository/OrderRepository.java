package com.popo2381.coffeeshop.domain.order.repository;

import com.popo2381.coffeeshop.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}