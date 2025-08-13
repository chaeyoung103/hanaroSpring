package com.example.hanaro.domain.cart.controller;

import com.example.hanaro.domain.cart.dto.request.CartItemRequestDto;
import com.example.hanaro.domain.cart.dto.request.CartItemUpdateRequestDto;
import com.example.hanaro.domain.cart.dto.response.CartResponseDto;
import com.example.hanaro.domain.cart.service.CartService;
import com.example.hanaro.global.response.BaseErrorResponse;
import com.example.hanaro.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[일반유저] 장바구니", description = "장바구니 관련 기능")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;
	private final com.example.hanaro.domain.user.repository.UserRepository userRepository;

	@Operation(summary = "장바구니에 상품 추가", description = "상품과 수량을 지정하여 내 장바구니에 담습니다.")
	@SecurityRequirement(name = "JWT Authentication")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "장바구니 추가 성공"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 사용자 또는 상품", content = @Content(schema = @Schema(implementation = BaseErrorResponse.class)))
	})
	@PostMapping("/items")
	public ResponseEntity<BaseResponse<Void>> addProductToCart(
		@Valid @RequestBody CartItemRequestDto requestDto) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userEmail = authentication.getName();

		Long userId = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new com.example.hanaro.domain.user.exception.UserException(com.example.hanaro.domain.user.exception.UserErrorCode.USER_NOT_FOUND))
			.getId();

		cartService.addProductToCart(userId, requestDto);
		return ResponseEntity.ok(new BaseResponse<>());
	}

	@Operation(summary = "내 장바구니 조회", description = "장바구니에 담긴 모든 상품 목록과 총액을 조회합니다.")
	@SecurityRequirement(name = "JWT Authentication")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "장바구니 조회 성공"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 사용자", content = @Content(schema = @Schema(implementation = BaseErrorResponse.class)))
	})
	@GetMapping
	public ResponseEntity<BaseResponse<CartResponseDto>> getCart() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userEmail = authentication.getName();

		Long userId = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new com.example.hanaro.domain.user.exception.UserException(com.example.hanaro.domain.user.exception.UserErrorCode.USER_NOT_FOUND))
			.getId();

		CartResponseDto cart = cartService.getCart(userId);
		return ResponseEntity.ok(new BaseResponse<>(cart));
	}

	@Operation(summary = "장바구니 상품 수량 변경", description = "장바구니에 담긴 특정 상품의 수량을 변경합니다.")
	@SecurityRequirement(name = "JWT Authentication")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "수량 변경 성공"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 장바구니 상품", content = @Content(schema = @Schema(implementation = BaseErrorResponse.class))),
		@ApiResponse(responseCode = "403", description = "본인의 장바구니 상품이 아님", content = @Content(schema = @Schema(implementation = BaseErrorResponse.class)))
	})
	@PatchMapping("/items/{cartItemId}")
	public ResponseEntity<BaseResponse<Void>> updateCartItemQuantity(
		@PathVariable Long cartItemId,
		@Valid @RequestBody CartItemUpdateRequestDto requestDto) {

		Long userId = getUserIdFromAuthentication();
		cartService.updateCartItemQuantity(userId, cartItemId, requestDto);
		return ResponseEntity.ok(new BaseResponse<>());
	}

	@Operation(summary = "장바구니 상품 삭제", description = "장바구니에서 특정 상품을 제거합니다.")
	@SecurityRequirement(name = "JWT Authentication")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "상품 삭제 성공"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 장바구니 상품", content = @Content(schema = @Schema(implementation = BaseErrorResponse.class))),
		@ApiResponse(responseCode = "403", description = "본인의 장바구니 상품이 아님", content = @Content(schema = @Schema(implementation = BaseErrorResponse.class)))
	})
	@DeleteMapping("/items/{cartItemId}")
	public ResponseEntity<BaseResponse<Void>> deleteCartItem(@PathVariable Long cartItemId) {
		Long userId = getUserIdFromAuthentication();
		cartService.deleteCartItem(userId, cartItemId);
		return ResponseEntity.ok(new BaseResponse<>());
	}

	@Operation(summary = "장바구니 삭제", description = "장바구니를 제거합니다.")
	@SecurityRequirement(name = "JWT Authentication")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "상품 삭제 성공"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 장바구니", content = @Content(schema = @Schema(implementation = BaseErrorResponse.class)))
	})
	@DeleteMapping
	public ResponseEntity<BaseResponse<Void>> deleteCart() {
		Long userId = getUserIdFromAuthentication();
		cartService.deleteCart(userId);
		return ResponseEntity.ok(new BaseResponse<>());
	}

	private Long getUserIdFromAuthentication() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userEmail = authentication.getName();
		return userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new com.example.hanaro.domain.user.exception.UserException(com.example.hanaro.domain.user.exception.UserErrorCode.USER_NOT_FOUND))
			.getId();
	}
}