package com.example.hanaro.domain.order.controller;



import java.util.List;

import com.example.hanaro.domain.order.dto.request.OrderSearchRequestDto;
import com.example.hanaro.domain.order.dto.response.OrderResponseDto;
import com.example.hanaro.domain.order.service.OrderService;
import com.example.hanaro.domain.user.repository.UserRepository;
import com.example.hanaro.global.response.BaseErrorResponse;
import com.example.hanaro.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[관리자] 주문", description = "주문 관련 기능")
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class OrderAdminController {

	private final OrderService orderService;
	private final UserRepository userRepository;

	@Operation(summary = "주문 내역 검색 및 조회 (관리자용)", description = "상품명, 주문 상태로 주문 내역을 검색합니다. 결과는 항상 최신순입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "주문 내역 검색 성공"),
		@ApiResponse(responseCode = "403", description = "접근 권한이 없습니다 (관리자만 사용 가능)", content = @Content(schema = @Schema(implementation = BaseErrorResponse.class)))
	})
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BaseResponse<List<OrderResponseDto>>> searchOrders(
		@ModelAttribute OrderSearchRequestDto searchDto) {
		List<OrderResponseDto> result = orderService.searchOrders(searchDto);
		return ResponseEntity.ok(new BaseResponse<>(result));
	}
}