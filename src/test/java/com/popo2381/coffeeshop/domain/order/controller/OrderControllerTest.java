package com.popo2381.coffeeshop.domain.order.controller;

import com.popo2381.coffeeshop.domain.order.external.OrderEventPayload;
import com.popo2381.coffeeshop.domain.order.external.OrderEventSender;
import com.popo2381.coffeeshop.domain.order.repository.OrderRepository;
import com.popo2381.coffeeshop.domain.point.repository.PointHistoryRepository;
import com.popo2381.coffeeshop.domain.point.repository.PointRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @MockitoBean
    private OrderEventSender orderEventSender;

    @Test
    @DisplayName("주문을 생성할 수 있다")
    void createOrder() throws Exception {
        // given
        chargePoint(1L, 10000);

        String requestBody = """
                {
                  "userId": 1,
                  "menuId": 1
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.menuId").value(1))
                .andExpect(jsonPath("$.paidPoint").value(3000))
                .andExpect(jsonPath("$.remainingPoint").value(7000));

        // 주문이 저장되었는지 확인
        assertThat(orderRepository.findAll()).hasSize(1);

        // 포인트가 차감되었는지 확인
        assertThat(pointRepository.findByUserId(1L)).isPresent();
        assertThat(pointRepository.findByUserId(1L).get().getBalance()).isEqualTo(7000);

        // 포인트 이력이 충전 1건 + 사용 1건 저장되었는지 확인
        assertThat(pointHistoryRepository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("존재하지 않는 사용자는 주문할 수 없다")
    void createOrderWithInvalidUser() throws Exception {
        // given
        String requestBody = """
                {
                  "userId": 999,
                  "menuId": 1
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 사용자입니다"));
    }

    @Test
    @DisplayName("존재하지 않는 메뉴로는 주문할 수 없다")
    void createOrderWithInvalidMenu() throws Exception {
        // given
        chargePoint(1L, 10000);

        String requestBody = """
                {
                  "userId": 1,
                  "menuId": 999
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MENU_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 메뉴입니다"));
    }

    @Test
    @DisplayName("포인트 정보가 없으면 주문할 수 없다")
    void createOrderWithoutPoint() throws Exception {
        // given
        String requestBody = """
                {
                  "userId": 1,
                  "menuId": 1
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("POINT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("포인트 정보를 찾을 수 없습니다"));
    }

    @Test
    @DisplayName("포인트가 부족하면 주문할 수 없다")
    void createOrderWithInsufficientPoint() throws Exception {
        // given
        chargePoint(1L, 1000);

        String requestBody = """
                {
                  "userId": 1,
                  "menuId": 1
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_POINT"))
                .andExpect(jsonPath("$.message").value("포인트가 부족합니다"));
    }

    @Test
    @DisplayName("주문 성공 시 주문 이벤트를 전송한다")
    void sendOrderEventWhenOrderCreated() throws Exception {
        // given
        chargePoint(1L, 10000);

        String requestBody = """
                {
                  "userId": 1,
                  "menuId": 1
                }
                """;

        // when
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        // then
        ArgumentCaptor<OrderEventPayload> captor = ArgumentCaptor.forClass(OrderEventPayload.class);
        verify(orderEventSender, times(1)).send(captor.capture());

        OrderEventPayload payload = captor.getValue();
        assertThat(payload.userId()).isEqualTo(1L);
        assertThat(payload.menuId()).isEqualTo(1L);
        assertThat(payload.price()).isEqualTo(3000);
    }

    // 주문 테스트 전에 포인트 충전 API를 먼저 호출해서 테스트 흐름을 맞춘다.
    private void chargePoint(Long userId, int amount) throws Exception {
        String requestBody = """
                {
                  "userId": %d,
                  "amount": %d
                }
                """.formatted(userId, amount);

        mockMvc.perform(post("/api/v1/points/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }
}