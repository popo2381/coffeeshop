package com.popo2381.coffeeshop.domain.order.entity;

import com.popo2381.coffeeshop.domain.menu.entity.Menu;
import com.popo2381.coffeeshop.domain.user.entity.User;
import com.popo2381.coffeeshop.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 주문한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 주문한 메뉴
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    // 주문 시점의 결제 금액
    @Column(nullable = false)
    private int price;

    // 현재 과제에서는 성공 주문만 저장하므로 COMPLETED만 사용
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    private Order(User user, Menu menu, int price, OrderStatus status) {
        this.user = user;
        this.menu = menu;
        this.price = price;
        this.status = status;
    }

    // 정상 결제가 완료된 주문 생성
    public static Order create(User user, Menu menu, int price) {
        return new Order(user, menu, price, OrderStatus.COMPLETED);
    }
}