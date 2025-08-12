package com.example.hanaro.repository;

import com.example.hanaro.domain.cart.repository.CartItemRepository;
import com.example.hanaro.domain.cart.repository.CartRepository;
import com.example.hanaro.domain.product.entity.Product;
import com.example.hanaro.domain.product.repository.ProductRepository;
import com.example.hanaro.domain.stats.entity.DailyProductStats;
import com.example.hanaro.domain.stats.repository.DailyProductStatsRepository;
import com.example.hanaro.domain.stats.repository.DailySalesStatsRepository;
import com.example.hanaro.domain.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DailyProductStatsRepositoryTest extends RepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DailySalesStatsRepository dailySalesStatsRepository;

    @Autowired
    private DailyProductStatsRepository dailyProductStatsRepository;

    private Product productA;
    private Product productB;

    @BeforeEach
    void setUp() {
        dailySalesStatsRepository.deleteAll();
        dailyProductStatsRepository.deleteAll();
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        userRepository.deleteAll();
        productRepository.deleteAll();

        productA = Product.builder().name("Product A").price(1000).stockQuantity(10).build();
        productB = Product.builder().name("Product B").price(2000).stockQuantity(20).build();
        productRepository.save(productA);
        productRepository.save(productB);
    }

    @Test
    @Order(1)
    void findAllByStatsDateTest() {
        LocalDate date = LocalDate.now();
        DailyProductStats stats1 = new DailyProductStats();
        stats1.setStatsDate(Date.valueOf(date));
        stats1.setProduct(productA);
        stats1.setTotalQuantitySold(5);
        stats1.setTotalRevenue(5000);

        DailyProductStats stats2 = new DailyProductStats();
        stats2.setStatsDate(Date.valueOf(date));
        stats2.setProduct(productB);
        stats2.setTotalQuantitySold(3);
        stats2.setTotalRevenue(6000);

        DailyProductStats statsOtherDay = new DailyProductStats();
        statsOtherDay.setStatsDate(Date.valueOf(date.minusDays(1)));
        statsOtherDay.setProduct(productA);
        statsOtherDay.setTotalQuantitySold(2);
        statsOtherDay.setTotalRevenue(2000);

        dailyProductStatsRepository.saveAll(List.of(stats1, stats2, statsOtherDay));

        List<DailyProductStats> results = dailyProductStatsRepository.findAllByStatsDate(Date.valueOf(date));

        assertThat(results).hasSize(2);
        assertThat(results).extracting(s -> s.getProduct().getName())
                .containsExactlyInAnyOrder("Product A", "Product B");
    }
}