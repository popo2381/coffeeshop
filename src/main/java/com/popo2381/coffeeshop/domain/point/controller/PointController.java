package com.popo2381.coffeeshop.domain.point.controller;

import com.popo2381.coffeeshop.domain.point.dto.request.PointChargeRequest;
import com.popo2381.coffeeshop.domain.point.dto.response.PointChargeResponse;
import com.popo2381.coffeeshop.domain.point.service.PointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @PostMapping("/charge")
    public ResponseEntity<PointChargeResponse> charge(@Valid @RequestBody PointChargeRequest request) {
        // 1. 요청값(userId, amount)을 서비스로 전달
        PointChargeResponse response = pointService.charge(request.userId(), request.amount());

        // 2. 충전 결과 응답 반환
        return ResponseEntity.ok(response);
    }
}