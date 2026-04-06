package com.popo2381.coffeeshop.domain.point.service;

import com.popo2381.coffeeshop.domain.point.dto.response.PointChargeResponse;
import com.popo2381.coffeeshop.domain.point.entity.Point;
import com.popo2381.coffeeshop.domain.point.entity.PointHistory;
import com.popo2381.coffeeshop.domain.point.repository.PointHistoryRepository;
import com.popo2381.coffeeshop.domain.point.repository.PointRepository;
import com.popo2381.coffeeshop.domain.user.entity.User;
import com.popo2381.coffeeshop.domain.user.repository.UserRepository;
import com.popo2381.coffeeshop.global.error.code.PointErrorCode;
import com.popo2381.coffeeshop.global.error.code.UserErrorCode;
import com.popo2381.coffeeshop.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PointService {

    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public PointChargeResponse charge(Long userId, int amount) {
        // 1. 충전 금액 검증
        if (amount < 1) {
            throw new BusinessException(PointErrorCode.INVALID_CHARGE_AMOUNT);
        }

        // 2. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // 3. 사용자 포인트 조회 또는 최초 생성
        Point point = pointRepository.findByUserIdForUpdate(userId)
                .orElseGet(() -> pointRepository.save(Point.create(user)));

        // 4. 포인트 충전
        point.charge(amount);

        // 5. 포인트 충전 이력 저장
        pointHistoryRepository.save(PointHistory.charge(user, amount));

        // 6. 응답 반환
        return new PointChargeResponse(userId, amount, point.getBalance());
    }
}