package com.example.hanaro.domain.stats.repository;

import java.sql.Date;
import java.util.List;

import com.example.hanaro.domain.stats.entity.DailyProductStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyProductStatsRepository extends JpaRepository<DailyProductStats, Long> {
	List<DailyProductStats> findAllByStatsDate(Date statsDate);
}