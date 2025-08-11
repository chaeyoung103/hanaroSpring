package com.example.hanaro.domain.stats.dto;

import com.example.hanaro.domain.stats.entity.DailyProductStats;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyProductStatsDto {
	private final String productName;
	private final int totalQuantitySold;
	private final int totalRevenue;

	public static DailyProductStatsDto fromEntity(DailyProductStats entity) {
		return DailyProductStatsDto.builder()
			.productName(entity.getProduct().getName())
			.totalQuantitySold(entity.getTotalQuantitySold())
			.totalRevenue(entity.getTotalRevenue())
			.build();
	}
}