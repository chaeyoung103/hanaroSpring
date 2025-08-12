package com.example.hanaro.repository;

import com.example.hanaro.domain.stats.entity.DailySalesStats;
import com.example.hanaro.domain.stats.repository.DailySalesStatsRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


public class DailySalesStatsRepositoryTest extends RepositoryTest {

    @Autowired
    private DailySalesStatsRepository dailySalesStatsRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Date testDate;

    @BeforeEach
    void setUp() {
        dailySalesStatsRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        testDate = Date.valueOf(LocalDate.of(2025, 8, 12));
        DailySalesStats stats = new DailySalesStats();
        stats.setStatsDate(testDate);
        stats.setTotalRevenue(100000);
        stats.setTotalOrders(25);
        entityManager.persistAndFlush(stats);
    }

    @Test
    @DisplayName("특정 날짜의 일일 매출 통계를 성공적으로 조회한다")
    void findByStatsDateTest() {
        Optional<DailySalesStats> found = dailySalesStatsRepository.findByStatsDate(Date.valueOf(LocalDate.of(2025, 8, 12)));

        assertThat(found).isPresent();
        assertThat(found.get().getTotalRevenue()).isEqualTo(100000);
        assertThat(found.get().getTotalOrders()).isEqualTo(25);
    }
}