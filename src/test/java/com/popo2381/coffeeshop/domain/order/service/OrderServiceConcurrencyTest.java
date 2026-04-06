package com.popo2381.coffeeshop.domain.order.service;

import com.popo2381.coffeeshop.domain.menu.entity.Menu;
import com.popo2381.coffeeshop.domain.menu.repository.MenuRepository;
import com.popo2381.coffeeshop.domain.order.external.OrderEventSender;
import com.popo2381.coffeeshop.domain.order.repository.OrderRepository;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderServiceConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private OrderEventSender orderEventSender;

    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_MENU_ID = 1L;

    @BeforeEach
    void setUp() {
        // 기존 테스트 데이터 영향 제거
        jdbcTemplate.update("delete from orders where user_id = ?", TEST_USER_ID);
        jdbcTemplate.update("delete from point_histories where user_id = ?", TEST_USER_ID);
        jdbcTemplate.update("delete from points where user_id = ?", TEST_USER_ID);
    }

    @Test
    @DisplayName("동시에 여러 주문 요청이 들어와도 포인트가 정확히 차감된다")
    void createOrder_concurrently() throws InterruptedException {
        // given
        User user = userRepository.findById(TEST_USER_ID).orElseThrow();
        Menu menu = menuRepository.findById(TEST_MENU_ID).orElseThrow();

        int initialBalance = 10_000;
        int menuPrice = menu.getPrice();
        int threadCount = 5;

        pointRepository.saveAndFlush(new Point(user, initialBalance));

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    orderService.create(TEST_USER_ID, TEST_MENU_ID);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        Point savedPoint = pointRepository.findByUserId(TEST_USER_ID).orElseThrow();

        long userOrderCount = orderRepository.findAll().stream()
                .filter(order -> order.getUser().getId().equals(TEST_USER_ID))
                .count();

        long userUseHistoryCount = pointHistoryRepository.findAll().stream()
                .filter(history -> history.getUser().getId().equals(TEST_USER_ID))
                .filter(history -> history.getType() == PointHistoryType.USE)
                .count();

        int expectedSuccessCount = initialBalance / menuPrice;
        int expectedFailCount = threadCount - expectedSuccessCount;
        int expectedRemainingBalance = initialBalance - (expectedSuccessCount * menuPrice);

        assertThat(successCount.get()).isEqualTo(expectedSuccessCount);
        assertThat(failCount.get()).isEqualTo(expectedFailCount);
        assertThat(savedPoint.getBalance()).isEqualTo(expectedRemainingBalance);
        assertThat(userOrderCount).isEqualTo(expectedSuccessCount);
        assertThat(userUseHistoryCount).isEqualTo(expectedSuccessCount);
    }
}