package com.example.hanaro.repository;

import com.example.hanaro.domain.order.entity.Order;
import com.example.hanaro.domain.order.entity.OrderStatus;
import com.example.hanaro.domain.order.repository.OrderItemRepository;
import com.example.hanaro.domain.order.repository.OrderRepository;
import com.example.hanaro.domain.product.entity.Product;
import com.example.hanaro.domain.product.repository.ProductRepository;
import com.example.hanaro.domain.stats.repository.DailyProductStatsRepository;
import com.example.hanaro.domain.stats.repository.DailySalesStatsRepository;
import com.example.hanaro.domain.user.entity.User;
import com.example.hanaro.domain.user.repository.UserRepository;
import com.example.hanaro.domain.order.entity.OrderItem;

import org.junit.jupiter.api.BeforeEach;
// Do not import org.junit.jupiter.api.Order to avoid conflict with the Order entity
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for {@link OrderItemRepository}.
 *
 * These tests verify the existsByProduct query.
 */
public class OrderItemRepositoryTest extends RepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DailySalesStatsRepository dailySalesStatsRepository;

    @Autowired
    private DailyProductStatsRepository dailyProductStatsRepository;

    private User testUser;
    private Product productA;
    private Product productB;

    @BeforeEach
    void setUp() {
        dailySalesStatsRepository.deleteAll();
        dailyProductStatsRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        userRepository.deleteAll();
        productRepository.deleteAll();

        testUser = User.builder()
                .email("orderitem@example.com")
                .password("password123")
                .nickname("orderItemTester")
                .role("ROLE_USER")
                .build();
        userRepository.save(testUser);

        productA = Product.builder().name("Product A").price(1000).stockQuantity(10).build();
        productB = Product.builder().name("Product B").price(2000).stockQuantity(20).build();
        productRepository.save(productA);
        productRepository.save(productB);
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void existsByProductTest() {
        // given an order containing productA
        Order order = new Order();
        order.setUser(testUser);
        order.setOrderDate(Timestamp.valueOf(LocalDateTime.now()));
        order.setStatus(OrderStatus.PAYED);
        order.setTotalPrice(3000);
        orderRepository.save(order);

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(productA);
        item.setPrice(productA.getPrice());
        item.setQuantity(3);
        orderItemRepository.save(item);

        boolean existsForA = orderItemRepository.existsByProduct(productA);
        boolean existsForB = orderItemRepository.existsByProduct(productB);

        assertThat(existsForA).isTrue();
        assertThat(existsForB).isFalse();
    }
}