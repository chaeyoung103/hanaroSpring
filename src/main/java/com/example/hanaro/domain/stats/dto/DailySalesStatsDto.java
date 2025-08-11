package com.example.hanaro.domain.stats.dto;

import com.example.hanaro.domain.stats.entity.DailySalesStats;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DailySalesStatsDto {
	private final int totalRevenue;
	private final int totalOrders;
	private final List<DailyProductStatsDto> productStats;

	public static DailySalesStatsDto fromEntity(DailySalesStats entity, List<DailyProductStatsDto> productStats) {
		return DailySalesStatsDto.builder()
			.totalRevenue(entity.getTotalRevenue())
			.totalOrders(entity.getTotalOrders())
			.productStats(productStats)
			.build();
	}
}