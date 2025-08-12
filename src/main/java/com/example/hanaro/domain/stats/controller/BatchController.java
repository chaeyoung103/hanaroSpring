package com.example.hanaro.domain.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class BatchController {

	private final JobLauncher jobLauncher;
	private final Job dailySalesStatsJob;

	@GetMapping("/api/batch/run")
	public String runBatchJob(@RequestParam(name = "date", required = false) String dateStr) throws Exception {

		String jobDate = (dateStr == null) ? LocalDate.now().minusDays(1).toString() : dateStr;

		JobParameters jobParameters = new JobParametersBuilder()
			.addString("yesterday", jobDate)
			.addLong("time", System.currentTimeMillis())
			.toJobParameters();

		jobLauncher.run(dailySalesStatsJob, jobParameters);

		return "Batch job has been invoked for date: " + jobDate;
	}
}