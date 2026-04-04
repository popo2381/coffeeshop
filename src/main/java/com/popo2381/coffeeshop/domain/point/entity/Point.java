package com.popo2381.coffeeshop.domain.point.entity;

import com.popo2381.coffeeshop.domain.user.entity.User;
import com.popo2381.coffeeshop.global.common.entity.BaseEntity;
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

    public static Point create(User user) {
        return new Point(user, 0);
    }

    public void charge(int amount) {
        this.balance += amount;
    }
}