package com.popo2381.coffeeshop.domain.point.entity;

import com.popo2381.coffeeshop.domain.user.entity.User;
import com.popo2381.coffeeshop.global.common.entity.BaseEntity;
import com.popo2381.coffeeshop.global.error.code.PointErrorCode;
import com.popo2381.coffeeshop.global.error.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "points")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private int balance;

    public Point(User user, int balance) {
        this.user = user;
        this.balance = balance;
    }

    // 최초 생성 시 포인트 잔액은 0으로 시작
    public static Point create(User user) {
        return new Point(user, 0);
    }

    // 포인트 충전
    public void charge(int amount) {
        this.balance += amount;
    }

    // 포인트 사용
    public void use(int amount) {
        // 잔액보다 큰 금액을 사용하려고 하면 예외 처리
        if (this.balance < amount) {
            throw new BusinessException(PointErrorCode.INSUFFICIENT_POINT);
        }
        this.balance -= amount;
    }
}