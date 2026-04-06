package com.popo2381.coffeeshop.domain.point.service;

import com.popo2381.coffeeshop.domain.point.entity.Point;
import com.popo2381.coffeeshop.domain.point.entity.PointHistoryType;
import com.popo2381.coffeeshop.domain.point.repository.PointHistoryRepository;
import com.popo2381.coffeeshop.domain.point.repository.PointRepository;
import com.popo2381.coffeeshop.domain.user.entity.User;
import com.popo2381.coffeeshop.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PointServiceConcurrencyTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Long TEST_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        // 기존 테스트 데이터 영향 제거
        jdbcTemplate.update("delete from orders where user_id = ?", TEST_USER_ID);
        jdbcTemplate.update("delete from point_histories where user_id = ?", TEST_USER_ID);
        jdbcTemplate.update("delete from points where user_id = ?", TEST_USER_ID);
    }

    @Test
    @DisplayName("동시에 여러 충전 요청이 들어와도 포인트가 정확히 누적된다")
    void chargePoint_concurrently() throws InterruptedException {
        // given
        User user = userRepository.findById(TEST_USER_ID).orElseThrow();

        pointRepository.saveAndFlush(new Point(user, 0));

        int threadCount = 10;
        int chargeAmount = 1000;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.charge(TEST_USER_ID, chargeAmount);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        Point savedPoint = pointRepository.findByUserId(TEST_USER_ID).orElseThrow();

        long userChargeHistoryCount = pointHistoryRepository.findAll().stream()
                .filter(history -> history.getUser().getId().equals(TEST_USER_ID))
                .filter(history -> history.getType() == PointHistoryType.CHARGE)
                .count();

        assertThat(savedPoint.getBalance()).isEqualTo(threadCount * chargeAmount);
        assertThat(userChargeHistoryCount).isEqualTo(threadCount);
    }
}