package com.example.hanaro.repository;

import com.example.hanaro.domain.cart.repository.CartItemRepository;
import com.example.hanaro.domain.order.repository.OrderItemRepository;
import com.example.hanaro.domain.order.repository.OrderRepository;
import com.example.hanaro.domain.product.entity.Product;
import com.example.hanaro.domain.product.entity.ProductImage;
import com.example.hanaro.domain.product.repository.ProductRepository;
import com.example.hanaro.domain.stats.repository.DailyProductStatsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for {@link ProductRepository}.
 *
 * These tests verify the custom query methods defined on the product repository.
 */
public class ProductRepositoryTest extends RepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private DailyProductStatsRepository dailyProductStatsRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private OrderRepository orderRepository;


    @BeforeEach
    void setUp() {
        // 외래 키를 가진 자식 테이블의 데이터를 먼저 삭제합니다.
        dailyProductStatsRepository.deleteAll();
        orderItemRepository.deleteAll();
        cartItemRepository.deleteAll();

        // 부모 테이블의 데이터를 삭제합니다.
        productRepository.deleteAll();
        // Product와 간접적으로 연관된 Order, User 데이터도 삭제해야 합니다.
        orderRepository.deleteAll();
    }

    @Test
    @Order(1)
    void findByNameTest() {
        // given a persisted product
        Product product = Product.builder()
            .name("Red Apple")
            .price(3000)
            .stockQuantity(100)
            .build();
        productRepository.save(product);

        // when searching by exact name
        Optional<Product> found = productRepository.findByName("Red Apple");

        // then the product should be found
        assertThat(found).isPresent();
        assertThat(found.get().getPrice()).isEqualTo(3000);
        assertThat(found.get().getStockQuantity()).isEqualTo(100);
    }

    @Test
    @Order(2)
    void findByNameContainingIgnoreCaseTest() {
        // given multiple products
        Product p1 = Product.builder().name("Green Tea").price(1500).stockQuantity(50).build();
        Product p2 = Product.builder().name("Black Tea").price(1600).stockQuantity(60).build();
        Product p3 = Product.builder().name("Herbal Tea").price(2000).stockQuantity(70).build();
        productRepository.saveAll(List.of(p1, p2, p3));

        // when searching for products containing 'tea' (case insensitive)
        List<Product> results = productRepository.findByNameContainingIgnoreCase("tEa");

        // then all tea products should be returned
        assertThat(results).hasSize(3);
        assertThat(results).extracting(Product::getName)
            .containsExactlyInAnyOrder("Green Tea", "Black Tea", "Herbal Tea");
    }
}