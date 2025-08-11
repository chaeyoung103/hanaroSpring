package com.example.hanaro.domain.order.controller;


import java.util.List;

import com.example.hanaro.domain.order.dto.response.OrderCreateResponseDto;
import com.example.hanaro.domain.order.dto.response.OrderResponseDto;
import com.example.hanaro.domain.order.service.OrderService;
import com.example.hanaro.domain.user.repository.UserRepository;
import com.example.hanaro.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[일반유저] 주문", description = "주문 관련 기능")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;
	private final UserRepository userRepository;

	@Operation(summary = "주문 생성", description = "장바구니에 담긴 모든 상품으로 주문을 생성합니다.")
	@SecurityRequirement(name = "JWT Authentication")
	@PostMapping
	public ResponseEntity<BaseResponse<OrderCreateResponseDto>> createOrder() {
		Long userId = getUserIdFromAuthentication();
		OrderCreateResponseDto response = orderService.createOrder(userId);
		return ResponseEntity.ok(new BaseResponse<>(response));
	}

	@Operation(summary = "내 주문 내역 조회", description = "자신의 모든 주문 내역을 최신순으로 조회합니다.")
	@SecurityRequirement(name = "JWT Authentication")
	@GetMapping
	public ResponseEntity<BaseResponse<List<OrderResponseDto>>> getMyOrders() {
		Long userId = getUserIdFromAuthentication();
		List<OrderResponseDto> response = orderService.findOrders(userId);
		return ResponseEntity.ok(new BaseResponse<>(response));
	}

	private Long getUserIdFromAuthentication() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userEmail = authentication.getName();
		return userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new com.example.hanaro.domain.user.exception.UserException(com.example.hanaro.domain.user.exception.UserErrorCode.USER_NOT_FOUND))
			.getId();
	}
}