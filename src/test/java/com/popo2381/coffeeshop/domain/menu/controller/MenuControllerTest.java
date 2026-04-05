package com.popo2381.coffeeshop.domain.menu.controller;

import com.popo2381.coffeeshop.domain.menu.entity.Menu;
import com.popo2381.coffeeshop.domain.menu.repository.MenuRepository;
import com.popo2381.coffeeshop.domain.order.entity.Order;
import com.popo2381.coffeeshop.domain.order.entity.OrderStatus;
import com.popo2381.coffeeshop.domain.order.repository.OrderRepository;
import com.popo2381.coffeeshop.domain.user.entity.User;
import com.popo2381.coffeeshop.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("메뉴 목록을 조회할 수 있다")
    void getMenus() throws Exception {
        mockMvc.perform(get("/api/v1/menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name",
                        containsInAnyOrder("아메리카노", "카페라떼", "카푸치노")));
    }

    @Test
    @DisplayName("최근 7일 인기 메뉴 Top3를 조회한다")
    void getPopularMenus() throws Exception {
        // given
        User user = userRepository.findById(1L).orElseThrow();

        Menu americano = menuRepository.findById(1L).orElseThrow();
        Menu latte = menuRepository.findById(2L).orElseThrow();
        Menu mocha = menuRepository.findById(3L).orElseThrow();

        saveOrder(user, americano, LocalDateTime.now().minusDays(1));
        saveOrder(user, americano, LocalDateTime.now().minusDays(2));
        saveOrder(user, americano, LocalDateTime.now().minusDays(3));

        saveOrder(user, latte, LocalDateTime.now().minusDays(1));
        saveOrder(user, latte, LocalDateTime.now().minusDays(2));

        saveOrder(user, mocha, LocalDateTime.now().minusDays(1));

        // 7일 초과 데이터 -> 제외되어야 함
        saveOrder(user, mocha, LocalDateTime.now().minusDays(8));

        // when & then
        mockMvc.perform(get("/api/v1/menus/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].menuId").value(americano.getId()))
                .andExpect(jsonPath("$[0].orderCount").value(3))
                .andExpect(jsonPath("$[1].menuId").value(latte.getId()))
                .andExpect(jsonPath("$[1].orderCount").value(2))
                .andExpect(jsonPath("$[2].menuId").value(mocha.getId()))
                .andExpect(jsonPath("$[2].orderCount").value(1));
    }

    private void saveOrder(User user, Menu menu, LocalDateTime createdAt) throws Exception {
        Order order = Order.create(user, menu, menu.getPrice());

        Field statusField = Order.class.getDeclaredField("status");
        statusField.setAccessible(true);
        statusField.set(order, OrderStatus.COMPLETED);

        Order savedOrder = orderRepository.saveAndFlush(order);

        jdbcTemplate.update(
                "update orders set created_at = ? where id = ?",
                createdAt,
                savedOrder.getId()
        );
    }
}