package com.example.hanaro.global.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {

	private final JobLauncher jobLauncher;
	private final Job dailySalesStatsJob;

	// 매일 자정에 실행되는 일일 매출 통계 배치 스케줄러

	@Scheduled(cron = "0 0 0 * * *")
	public void runDailySalesStatsJob() {
		String yesterday = LocalDate.now().minusDays(1).toString();
		log.info(">>>>>> {} 일자 일일 매출 통계 배치 스케줄러 실행", yesterday);

		try {
			JobParameters jobParameters = new JobParametersBuilder()
				.addString("yesterday", yesterday)
				.addLong("time", System.currentTimeMillis())
				.toJobParameters();

			jobLauncher.run(dailySalesStatsJob, jobParameters);

		} catch (Exception e) {
			log.error("일일 매출 통계 배치 실행 중 오류 발생", e);
		}
	}
}