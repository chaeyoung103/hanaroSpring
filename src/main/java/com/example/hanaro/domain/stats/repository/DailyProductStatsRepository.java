package com.example.hanaro.domain.stats.repository;

import com.example.hanaro.domain.stats.entity.DailyProductStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyProductStatsRepository extends JpaRepository<DailyProductStats, Long> {
}
