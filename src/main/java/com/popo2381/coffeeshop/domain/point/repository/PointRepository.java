package com.popo2381.coffeeshop.domain.point.repository;

import com.popo2381.coffeeshop.domain.point.entity.Point;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {
    Optional<Point> findByUserId(Long userId);

    // 포인트 충전/차감 시 동시성 제어를 위해 비관적 락으로 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Point> findByUserIdForUpdate(Long userId);

}