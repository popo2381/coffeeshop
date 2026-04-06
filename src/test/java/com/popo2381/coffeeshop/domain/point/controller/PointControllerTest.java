package com.popo2381.coffeeshop.domain.point.controller;

import com.popo2381.coffeeshop.domain.point.entity.PointHistory;
import com.popo2381.coffeeshop.domain.point.repository.PointHistoryRepository;
import com.popo2381.coffeeshop.domain.point.repository.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @BeforeEach
    void setUp() {
        pointHistoryRepository.deleteAll();
        pointRepository.deleteAll();
    }

    @Test
    @DisplayName("포인트를 충전할 수 있다")
    void chargePoint() throws Exception {
        // given
        String requestBody = """
                {
                  "userId": 1,
                  "amount": 5000
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/points/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.chargedAmount").value(5000))
                .andExpect(jsonPath("$.balance").value(5000));

        // 포인트 엔티티가 생성되고 잔액이 반영되었는지 확인
        assertThat(pointRepository.findByUserId(1L)).isPresent();
        assertThat(pointRepository.findByUserId(1L).orElseThrow().getBalance()).isEqualTo(5000);

        // 포인트 충전 이력이 저장되었는지 확인
        PointHistory history = pointHistoryRepository.findAll().stream()
                .filter(pointHistory -> pointHistory.getUser().getId().equals(1L))
                .findFirst()
                .orElseThrow();

        assertThat(pointHistoryRepository.findAll().stream()
                .filter(pointHistory -> pointHistory.getUser().getId().equals(1L))
                .count()).isEqualTo(1);
        assertThat(history.getAmount()).isEqualTo(5000);
    }

    @Test
    @DisplayName("존재하지 않는 사용자는 포인트를 충전할 수 없다")
    void chargePointWithInvalidUser() throws Exception {
        // given
        String requestBody = """
                {
                  "userId": 999,
                  "amount": 5000
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/points/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 사용자입니다"));
    }

    @Test
    @DisplayName("충전 금액이 1 미만이면 요청이 실패한다")
    void chargePointWithInvalidAmount() throws Exception {
        // given
        String requestBody = """
                {
                  "userId": 1,
                  "amount": 0
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/points/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT_VALUE"))
                .andExpect(jsonPath("$.message").value("요청 값이 올바르지 않습니다"));
    }
}