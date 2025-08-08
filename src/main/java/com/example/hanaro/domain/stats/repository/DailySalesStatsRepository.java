package com.example.hanaro.domain.stats.repository;

import com.example.hanaro.domain.stats.entity.DailySalesStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.Optional;

public interface DailySalesStatsRepository extends JpaRepository<DailySalesStats, Long> {
    Optional<DailySalesStats> findByStatsDate(Date statsDate);
}
