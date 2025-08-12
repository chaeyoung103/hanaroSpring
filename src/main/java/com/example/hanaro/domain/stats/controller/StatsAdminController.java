package com.example.hanaro.domain.stats.controller;

import com.example.hanaro.domain.stats.dto.DailySalesStatsDto;
import com.example.hanaro.domain.stats.service.StatsService;
import com.example.hanaro.global.response.BaseErrorResponse;
import com.example.hanaro.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "[관리자] 통계", description = "매출 통계 관련 기능")
@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT Authentication")
public class StatsAdminController {

	private final StatsService statsService;

	@Operation(summary = "일일 매출 통계 조회", description = "특정 날짜의 일일 매출 및 상품별 판매 통계를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "통계 조회 성공"),
		@ApiResponse(responseCode = "403", description = "접근 권한이 없습니다 (관리자만 사용 가능)", content = @Content(schema = @Schema(implementation = BaseErrorResponse.class)))
	})
	@GetMapping("/daily-sales")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BaseResponse<DailySalesStatsDto>> getDailySalesStats(
		@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

		DailySalesStatsDto result = statsService.getDailyStats(date);
		return ResponseEntity.ok(new BaseResponse<>(result));
	}
}