package com.popo2381.coffeeshop.domain.menu.repository;

import com.popo2381.coffeeshop.domain.menu.dto.response.PopularMenuResponse;
import com.popo2381.coffeeshop.domain.menu.entity.QMenu;
import com.popo2381.coffeeshop.domain.order.entity.OrderStatus;
import com.popo2381.coffeeshop.domain.order.entity.QOrder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class MenuQueryRepositoryImpl implements MenuQueryRepository {

    private final JPAQueryFactory queryFactory;

    public MenuQueryRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<PopularMenuResponse> findTop3PopularMenusByLast7Days() {
        QOrder order = QOrder.order;
        QMenu menu = QMenu.menu;

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        return queryFactory
                .select(Projections.constructor(
                        PopularMenuResponse.class,
                        menu.id,
                        menu.name,
                        menu.price,
                        order.count()
                ))
                .from(order)
                .join(order.menu, menu)
                .where(
                        order.status.eq(OrderStatus.COMPLETED),
                        order.createdAt.goe(sevenDaysAgo)
                )
                .groupBy(menu.id, menu.name, menu.price)
                .orderBy(order.count().desc(), menu.id.asc())
                .limit(3)
                .fetch();
    }
}