package com.example.hanaro.domain.stats.service;

import com.example.hanaro.domain.stats.dto.DailySalesStatsDto;
import java.time.LocalDate;

public interface StatsService {
	/**
	 * 특정 날짜의 일일 매출 통계를 조회합니다.
	 * @param date 조회할 날짜
	 * @return 일일 매출 통계 DTO
	 */
	DailySalesStatsDto getDailyStats(LocalDate date);
}