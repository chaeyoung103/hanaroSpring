package com.example.hanaro.domain.product.controller;

import com.example.hanaro.domain.product.dto.response.ProductDto;
import com.example.hanaro.domain.product.service.ProductService;
import com.example.hanaro.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "[일반유저] 상품", description = "상품 관련 API (사용자용)")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

	private final ProductService productService;

	@Operation(summary = "상품 목록 검색 (사용자용)", description = "키워드로 상품을 검색합니다. 키워드가 없으면 전체 목록이 조회됩니다.")
	@SecurityRequirement(name = "JWT Authentication")
	@GetMapping("/search")
	public ResponseEntity<BaseResponse<List<ProductDto>>> searchProducts(
		@RequestParam(required = false) String keyword) {
		List<ProductDto> products = productService.searchProducts(keyword);
		return ResponseEntity.ok(new BaseResponse<>(products));
	}

	@Operation(summary = "상품 상세 조회 (로그인 유저 공용)", description = "상품 ID로 특정 상품의 상세 정보를 조회합니다.")
	@SecurityRequirement(name = "JWT Authentication")
	@GetMapping("/{productId}")
	public ResponseEntity<BaseResponse<ProductDto>> getProductById(@PathVariable Long productId) {
		ProductDto product = productService.findProductById(productId);
		return ResponseEntity.ok(new BaseResponse<>(product));
	}
}