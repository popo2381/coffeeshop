package com.popo2381.coffeeshop.domain.point.entity;

import com.popo2381.coffeeshop.domain.user.entity.User;
import com.popo2381.coffeeshop.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "point_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PointHistoryType type;

    @Column(nullable = false)
    private int amount;

    private PointHistory(User user, PointHistoryType type, int amount) {
        this.user = user;
        this.type = type;
        this.amount = amount;
    }

    public static PointHistory charge(User user, int amount) {
        return new PointHistory(user, PointHistoryType.CHARGE, amount);
    }

    public static PointHistory use(User user, int amount) {
        return new PointHistory(user, PointHistoryType.USE, amount);
    }
}