package com.example.hanaro.domain.stats.service;

import com.example.hanaro.domain.stats.dto.DailyProductStatsDto;
import com.example.hanaro.domain.stats.dto.DailySalesStatsDto;
import com.example.hanaro.domain.stats.entity.DailyProductStats;
import com.example.hanaro.domain.stats.entity.DailySalesStats;
import com.example.hanaro.domain.stats.repository.DailyProductStatsRepository;
import com.example.hanaro.domain.stats.repository.DailySalesStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

	private final DailySalesStatsRepository dailySalesStatsRepository;
	private final DailyProductStatsRepository dailyProductStatsRepository;

	@Override
	@Transactional(readOnly = true)
	public DailySalesStatsDto getDailyStats(LocalDate date) {
		Date sqlDate = Date.valueOf(date);

		Optional<DailySalesStats> dailySalesStatsOpt = dailySalesStatsRepository.findByStatsDate(sqlDate);

		if (dailySalesStatsOpt.isEmpty()) {
			return DailySalesStatsDto.builder()
				.totalRevenue(0)
				.totalOrders(0)
				.productStats(Collections.emptyList())
				.build();
		}

		List<DailyProductStats> productStatsEntities = dailyProductStatsRepository.findAllByStatsDate(sqlDate);
		List<DailyProductStatsDto> productStatsDtos = productStatsEntities.stream()
			.map(DailyProductStatsDto::fromEntity)
			.collect(Collectors.toList());

		return DailySalesStatsDto.fromEntity(dailySalesStatsOpt.get(), productStatsDtos);
	}
}