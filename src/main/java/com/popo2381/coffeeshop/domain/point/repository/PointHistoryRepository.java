package com.popo2381.coffeeshop.domain.point.repository;

import com.popo2381.coffeeshop.domain.point.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
}