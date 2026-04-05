package com.popo2381.coffeeshop.domain.order.controller;

import com.popo2381.coffeeshop.domain.order.dto.request.OrderCreateRequest;
import com.popo2381.coffeeshop.domain.order.dto.response.OrderCreateResponse;
import com.popo2381.coffeeshop.domain.order.dto.response.OrderDetailResponse;
import com.popo2381.coffeeshop.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 생성 API
    @PostMapping
    public ResponseEntity<OrderCreateResponse> create(@Valid @RequestBody OrderCreateRequest request) {

        // 1. 주문 생성 서비스 호출
        OrderCreateResponse response = orderService.create(
                request.userId(),
                request.menuId()
        );

        // 2. 주문 생성 결과 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 주문 단건 조회 API
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }
}