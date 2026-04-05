package com.popo2381.coffeeshop.domain.order.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MockOrderEventSender implements OrderEventSender {

    @Override
    public void send(OrderEventPayload payload) {
        // 현재 단계에서는 외부 플랫폼 대신 로그로 전송을 대체
        log.info(
                "[MOCK_ORDER_EVENT] userId={}, menuId={}, price={}",
                payload.userId(),
                payload.menuId(),
                payload.price()
        );
    }
}