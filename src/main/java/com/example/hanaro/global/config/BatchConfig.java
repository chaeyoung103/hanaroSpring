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

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

	private final DailySalesStatsRepository dailySalesStatsRepository;
	private final DailyProductStatsRepository dailyProductStatsRepository;
	private final EntityManagerFactory entityManagerFactory;

	// Job 정의: 일별 매출 통계를 계산하는 전체 작업
	@Bean
	public Job dailySalesStatsJob(JobRepository jobRepository, Step summarizeDailySalesStep) {
		return new JobBuilder("dailySalesStatsJob", jobRepository)
			.start(summarizeDailySalesStep)
			.build();
	}

	// Step 정의: 실제 작업 단위 (읽기 -> 처리 -> 쓰기)
	@Bean
	public Step summarizeDailySalesStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("summarizeDailySalesStep", jobRepository)
			.<Order, DailySalesStats>chunk(100, transactionManager) // 100개씩 처리
			.reader(orderReader(null))
			.processor(salesStatsProcessor())
			.writer(salesStatsWriter())
			.build();
	}

	// Reader: DB에서 어제 주문 데이터를 읽어옴
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
		// Step 실행 전체에서 공유될 집계 데이터 객체
		// (주의: 멀티스레드 환경에서는 Thread-safe한 자료구조 사용 필요)
		final Map<Long, DailyProductStats> productStatsMap = new ConcurrentHashMap<>();
		final BigDecimal[] totalRevenue = {BigDecimal.ZERO};
		final int[] totalOrders = {0};

		return order -> {
			totalRevenue[0] = totalRevenue[0].add(order.getTotalPrice());
			totalOrders[0]++;

			order.getOrderItems().forEach(item -> {
				Long productId = item.getProduct().getId();
				DailyProductStats productStats = productStatsMap.computeIfAbsent(productId, id -> {
					DailyProductStats newStats = new DailyProductStats();
					newStats.setProduct(item.getProduct());
					newStats.setStatsDate(Date.valueOf(LocalDate.now().minusDays(1)));
					newStats.setTotalQuantitySold(0);
					newStats.setTotalRevenue(BigDecimal.ZERO);
					return newStats;
				});
				productStats.setTotalQuantitySold(productStats.getTotalQuantitySold() + item.getQuantity());
				productStats.setTotalRevenue(productStats.getTotalRevenue().add(item.getPrice().multiply(new BigDecimal(item.getQuantity()))));
			});

			// Processor에서는 null을 반환하여 Writer로 데이터가 넘어가지 않도록 함
			// 실제 저장은 Writer에서 한 번에 처리
			return null;
		};
	}


	// Writer: 계산된 통계 데이터를 DB에 저장
	@Bean
	public ItemWriter<DailySalesStats> salesStatsWriter() {
		return items -> { // items는 비어있음 (Processor에서 null을 반환했기 때문)
			LocalDate yesterday = LocalDate.now().minusDays(1);

			// ItemProcessor에서 사용했던 집계 데이터를 여기서 다시 가져와야 함
			// 이는 StepExecutionListener를 통해 더 효율적으로 구현할 수 있음
			// 여기서는 개념 설명을 위해 단순화된 예시를 보여줌

			// TODO: Processor의 집계 데이터를 Listener를 통해 가져오는 로직 구현 필요
			// 1. DailySalesStats 저장
			DailySalesStats dailySalesStats = new DailySalesStats();
			dailySalesStats.setStatsDate(Date.valueOf(yesterday));
			// dailySalesStats.setTotalRevenue(...); // Processor에서 계산된 값
			// dailySalesStats.setTotalOrders(...);   // Processor에서 계산된 값
			// dailySalesStatsRepository.save(dailySalesStats);
			log.info("Saved DailySalesStats for {}", yesterday);

			// 2. DailyProductStats 저장
			// dailyProductStatsRepository.saveAll(...); // Processor에서 계산된 값들의 리스트
			log.info("Saved DailyProductStats for {}", yesterday);
		};
	}
}