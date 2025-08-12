package com.example.hanaro.global.batch;

import com.example.hanaro.domain.stats.entity.DailyProductStats;
import com.example.hanaro.domain.stats.entity.DailySalesStats;
import com.example.hanaro.domain.stats.repository.DailyProductStatsRepository;
import com.example.hanaro.domain.stats.repository.DailySalesStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.batch.core.JobParameters;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobCompletionNotificationListener implements JobExecutionListener {

	private final DailySalesStatsRepository dailySalesStatsRepository;
	private final DailyProductStatsRepository dailyProductStatsRepository;

	// 배치 작업 동안 집계 데이터를 임시보관
	private final Map<Long, DailyProductStats> productStatsMap = new ConcurrentHashMap<>();
	private int totalRevenue = 0;
	private int totalOrders = 0;

	@Override
	public void beforeJob(JobExecution jobExecution) {
		log.info("!!! DAILY SALES STATS JOB START!");
		productStatsMap.clear();
		totalRevenue = 0;
		totalOrders = 0;
	}

	@Override
	@Transactional
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! DAILY SALES STATS JOB FINISHED!");

			JobParameters jobParameters = jobExecution.getJobParameters();
			String dateStr = jobParameters.getString("yesterday");
			LocalDate jobDate = LocalDate.parse(dateStr);
			Date statsDate = Date.valueOf(jobDate);

			// 일일 전체 매출 통계 저장
			DailySalesStats dailySalesStats = dailySalesStatsRepository.findByStatsDate(statsDate)
				.orElse(new DailySalesStats());

			dailySalesStats.setStatsDate(statsDate);
			dailySalesStats.setTotalRevenue(totalRevenue);
			dailySalesStats.setTotalOrders(totalOrders);
			dailySalesStatsRepository.save(dailySalesStats);
			log.info("Saved DailySalesStats for {}: Total Revenue = {}, Total Orders = {}", statsDate, totalRevenue, totalOrders);

			// 일일 상품별 통계 저장 (날짜를 정확히 지정해줘야됨)
			productStatsMap.values().forEach(stats -> stats.setStatsDate(statsDate));
			dailyProductStatsRepository.saveAll(productStatsMap.values());
			log.info("Saved {} DailyProductStats entries for date {}.", productStatsMap.size(), statsDate);
		}
	}


	public void aggregate(com.example.hanaro.domain.order.entity.Order order) {
		totalRevenue += order.getTotalPrice();
		totalOrders++;

		order.getOrderItems().forEach(item -> {
			Long productId = item.getProduct().getId();
			DailyProductStats productStats = productStatsMap.computeIfAbsent(productId, id -> {
				DailyProductStats newStats = new DailyProductStats();
				newStats.setProduct(item.getProduct());
				newStats.setTotalQuantitySold(0);
				newStats.setTotalRevenue(0);
				return newStats;
			});
			productStats.setTotalQuantitySold(productStats.getTotalQuantitySold() + item.getQuantity());
			int itemRevenue = item.getPrice() * item.getQuantity();
			productStats.setTotalRevenue(productStats.getTotalRevenue() + itemRevenue);
		});
	}
}