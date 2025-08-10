package com.example.hanaro.global.config;

import com.example.hanaro.domain.order.entity.Order;
import com.example.hanaro.domain.stats.entity.DailyProductStats;
import com.example.hanaro.domain.stats.entity.DailySalesStats;
import com.example.hanaro.domain.stats.repository.DailyProductStatsRepository;
import com.example.hanaro.domain.stats.repository.DailySalesStatsRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

	private final DailySalesStatsRepository dailySalesStatsRepository;
	private final DailyProductStatsRepository dailyProductStatsRepository;
	private final EntityManagerFactory entityManagerFactory;

	// ... Job, Step, Reader는 그대로 ...
	@Bean
	public Job dailySalesStatsJob(JobRepository jobRepository, Step summarizeDailySalesStep) {
		return new JobBuilder("dailySalesStatsJob", jobRepository)
			.start(summarizeDailySalesStep)
			.build();
	}

	@Bean
	public Step summarizeDailySalesStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("summarizeDailySalesStep", jobRepository)
			.<Order, DailySalesStats>chunk(100, transactionManager)
			.reader(orderReader(null))
			.processor(salesStatsProcessor())
			.writer(salesStatsWriter())
			.build();
	}

	@Bean
	@StepScope
	public JpaPagingItemReader<Order> orderReader(@Value("#{jobParameters['yesterday']}") String yesterdayStr) {
		LocalDate yesterday = LocalDate.parse(yesterdayStr);
		Map<String, Object> params = new HashMap<>();
		params.put("startDate", java.sql.Timestamp.valueOf(yesterday.atStartOfDay()));
		params.put("endDate", java.sql.Timestamp.valueOf(yesterday.atTime(LocalTime.MAX)));

		return new JpaPagingItemReaderBuilder<Order>()
			.name("orderReader")
			.entityManagerFactory(entityManagerFactory)
			.pageSize(100)
			.queryString("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
			.parameterValues(params)
			.build();
	}

	// Processor: 읽어온 주문 데이터를 통계 데이터로 가공
	@Bean
	public ItemProcessor<Order, DailySalesStats> salesStatsProcessor() {
		final Map<Long, DailyProductStats> productStatsMap = new ConcurrentHashMap<>();
		// [수정] BigDecimal[] -> int[]
		final int[] totalRevenue = {0};
		final int[] totalOrders = {0};

		return order -> {
			// [수정] .add() -> + 연산
			totalRevenue[0] += order.getTotalPrice();
			totalOrders[0]++;

			order.getOrderItems().forEach(item -> {
				Long productId = item.getProduct().getId();
				DailyProductStats productStats = productStatsMap.computeIfAbsent(productId, id -> {
					DailyProductStats newStats = new DailyProductStats();
					newStats.setProduct(item.getProduct());
					newStats.setStatsDate(Date.valueOf(LocalDate.now().minusDays(1)));
					newStats.setTotalQuantitySold(0);
					// [수정] BigDecimal.ZERO -> 0
					newStats.setTotalRevenue(0);
					return newStats;
				});
				productStats.setTotalQuantitySold(productStats.getTotalQuantitySold() + item.getQuantity());
				// [수정] BigDecimal 연산 -> int 연산
				int itemRevenue = item.getPrice() * item.getQuantity();
				productStats.setTotalRevenue(productStats.getTotalRevenue() + itemRevenue);
			});

			return null;
		};
	}

	// Writer: (이전과 동일)
	@Bean
	public ItemWriter<DailySalesStats> salesStatsWriter() {
		return items -> {
			log.info("Batch job finished. Aggregated data should be saved via a listener.");
		};
	}
}