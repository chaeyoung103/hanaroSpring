package com.example.hanaro.repository;

import com.example.hanaro.domain.order.entity.Order;
import com.example.hanaro.domain.order.entity.OrderStatus;
import com.example.hanaro.domain.order.repository.OrderRepository;
import com.example.hanaro.domain.user.entity.User;
import com.example.hanaro.domain.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager; // TestEntityManager 임포트 추가
import org.springframework.test.annotation.Rollback; // Rollback 임포트 추가

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderRepositoryTest extends RepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
            .email("orderuser@example.com")
            .password("password123")
            .nickname("orderTester")
            .role("ROLE_USER")
            .build();
        userRepository.save(testUser);
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("사용자별 주문 목록을 최신순으로 조회한다")
    void findAllByUserOrderByOrderDateDescTest() {
        Order olderOrder = new Order();
        olderOrder.setUser(testUser);
        olderOrder.setOrderDate(Timestamp.valueOf(LocalDateTime.now().minusHours(2)));
        olderOrder.setStatus(OrderStatus.PAYED);
        olderOrder.setTotalPrice(10000);

        Order newerOrder = new Order();
        newerOrder.setUser(testUser);
        newerOrder.setOrderDate(Timestamp.valueOf(LocalDateTime.now().minusHours(1)));
        newerOrder.setStatus(OrderStatus.PAYED);
        newerOrder.setTotalPrice(20000);

        orderRepository.saveAll(List.of(olderOrder, newerOrder));

        List<Order> orders = orderRepository.findAllByUserOrderByOrderDateDesc(testUser);

        assertThat(orders).hasSize(2);
        assertThat(orders.get(0).getOrderDate()).isAfter(orders.get(1).getOrderDate());
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @Transactional
    @DisplayName("주문 상태와 날짜를 기준으로 주문 상태를 업데이트한다")
    void updateOrderStatusByStatusAndDateTest() {
        Order order1 = new Order();
        order1.setUser(testUser);
        order1.setOrderDate(Timestamp.valueOf(LocalDateTime.now().minusMinutes(30)));
        order1.setStatus(OrderStatus.PAYED);
        order1.setTotalPrice(5000);

        Order order2 = new Order();
        order2.setUser(testUser);
        order2.setOrderDate(Timestamp.valueOf(LocalDateTime.now().minusMinutes(20)));
        order2.setStatus(OrderStatus.PAYED);
        order2.setTotalPrice(7000);

        Order order3 = new Order();
        order3.setUser(testUser);
        order3.setOrderDate(Timestamp.valueOf(LocalDateTime.now().minusMinutes(10)));
        order3.setStatus(OrderStatus.PREPARING);
        order3.setTotalPrice(9000);

        orderRepository.saveAll(List.of(order1, order2, order3));

        entityManager.flush();
        entityManager.clear();

        LocalDateTime threshold = LocalDateTime.now().plusHours(1); // 충분히 미래의 시간을 기준으로 설정
        int updatedCount = orderRepository.updateOrderStatusByStatusAndDate(
            OrderStatus.PAYED, OrderStatus.PREPARING, threshold);

        assertThat(updatedCount).isEqualTo(2);

        entityManager.clear();

        List<Order> orders = orderRepository.findAll();
        long preparingCount = orders.stream()
            .filter(o -> o.getStatus() == OrderStatus.PREPARING)
            .count();

        assertThat(preparingCount).isEqualTo(3);
    }
}