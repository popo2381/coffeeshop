package com.popo2381.coffeeshop.domain.order.service;

import com.popo2381.coffeeshop.domain.menu.entity.Menu;
import com.popo2381.coffeeshop.domain.menu.repository.MenuRepository;
import com.popo2381.coffeeshop.domain.order.dto.response.OrderCreateResponse;
import com.popo2381.coffeeshop.domain.order.entity.Order;
import com.popo2381.coffeeshop.domain.order.repository.OrderRepository;
import com.popo2381.coffeeshop.domain.point.entity.Point;
import com.popo2381.coffeeshop.domain.point.entity.PointHistory;
import com.popo2381.coffeeshop.domain.point.repository.PointHistoryRepository;
import com.popo2381.coffeeshop.domain.point.repository.PointRepository;
import com.popo2381.coffeeshop.domain.user.entity.User;
import com.popo2381.coffeeshop.domain.user.repository.UserRepository;
import com.popo2381.coffeeshop.global.error.code.MenuErrorCode;
import com.popo2381.coffeeshop.global.error.code.PointErrorCode;
import com.popo2381.coffeeshop.global.error.code.UserErrorCode;
import com.popo2381.coffeeshop.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final PointRepository pointRepository;
    private final OrderRepository orderRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public OrderCreateResponse create(Long userId, Long menuId) {
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // 2. 메뉴 조회
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new BusinessException(MenuErrorCode.MENU_NOT_FOUND));

        // 3. 사용자 포인트 조회
        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(PointErrorCode.POINT_NOT_FOUND));

        // 4. 메뉴 가격만큼 포인트 차감
        point.use(menu.getPrice());

        // 5. 주문 생성 및 저장
        Order order = Order.create(user, menu, menu.getPrice());
        Order savedOrder = orderRepository.save(order);

        // 6. 포인트 사용 이력 저장
        pointHistoryRepository.save(PointHistory.use(user, menu.getPrice()));

        // 7. 응답 반환
        return new OrderCreateResponse(
                savedOrder.getId(),
                user.getId(),
                menu.getId(),
                menu.getPrice(),
                point.getBalance()
        );
    }
}