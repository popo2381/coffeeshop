package com.popo2381.coffeeshop.domain.order.external;

// 주문 내역을 외부 데이터 수집 플랫폼으로 전송하는 역할
public interface OrderEventSender {

    void send(OrderEventPayload payload);
}