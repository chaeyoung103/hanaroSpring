package com.example.hanaro.global.config;

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
	private final Job dailySalesStatsJob; // BatchConfig에 정의된 Job을 주입받음

	/**
	 * 매일 자정에 일일 매출 통계 배치를 실행합니다.
	 */
	@Scheduled(cron = "0 0 0 * * *") // 매일 00:00:00에 실행
	public void runDailySalesStatsJob() {
		// 어제 날짜를 잡 파라미터로 설정
		String yesterday = LocalDate.now().minusDays(1).toString();
		log.info(">>>>>> {} 일자 일일 매출 통계 배치 스케줄러 실행", yesterday);

		try {
			JobParameters jobParameters = new JobParametersBuilder()
				.addString("yesterday", yesterday)
				.addLong("time", System.currentTimeMillis()) // 잡 파라미터는 매번 달라야 하므로 시간 추가
				.toJobParameters();

			jobLauncher.run(dailySalesStatsJob, jobParameters);

		} catch (Exception e) {
			log.error("일일 매출 통계 배치 실행 중 오류 발생", e);
		}
	}
}