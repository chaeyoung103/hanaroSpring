package com.example.hanaro.global.config;

import com.example.hanaro.domain.order.entity.Order;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

	private final EntityManagerFactory entityManagerFactory;
	private final JobCompletionNotificationListener listener;

	@Bean
	public Job dailySalesStatsJob(JobRepository jobRepository, Step summarizeDailySalesStep) {
		return new JobBuilder("dailySalesStatsJob", jobRepository)
			.listener(listener)
			.start(summarizeDailySalesStep)
			.build();
	}

	@Bean
	public Step summarizeDailySalesStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("summarizeDailySalesStep", jobRepository)
			.<Order, Order>chunk(100, transactionManager) // [✨수정✨] Processor가 없으므로 <Order, Order>
			.reader(orderReader(null))
			// .processor(salesStatsProcessor()) // [✨제거✨] Processor는 더 이상 필요 없음
			.writer(salesStatsWriter()) // [✨수정✨] Writer의 역할이 변경됨
			.build();
	}

	@Bean
	@StepScope
	public JpaPagingItemReader<Order> orderReader(@Value("#{jobParameters['yesterday']}") String yesterdayStr) {
		LocalDate yesterday = (yesterdayStr != null) ? LocalDate.parse(yesterdayStr) : LocalDate.now().minusDays(1);

		log.info(">>>> JpaPagingItemReader started for date: {}", yesterday);

		Map<String, Object> params = new HashMap<>();
		params.put("startDate", java.sql.Timestamp.valueOf(yesterday.atStartOfDay()));
		params.put("endDate", java.sql.Timestamp.valueOf(yesterday.atTime(LocalTime.MAX)));

		return new JpaPagingItemReaderBuilder<Order>()
			.name("orderReader")
			.entityManagerFactory(entityManagerFactory)
			.pageSize(100)
			.queryString("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
			.parameterValues(params)
			.build();
	}

	@Bean
	public ItemWriter<Order> salesStatsWriter() {
		return items -> {
			log.info("Aggregating {} items...", items.size());
			items.forEach(listener::aggregate);
		};
	}
}