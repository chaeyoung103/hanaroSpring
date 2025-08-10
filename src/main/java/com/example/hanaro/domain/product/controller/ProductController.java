package com.example.hanaro.domain.product.controller;

import com.example.hanaro.domain.product.dto.request.ProductCreateRequestDto;
import com.example.hanaro.domain.product.dto.response.ProductDto;
import com.example.hanaro.domain.product.service.ProductService;
import com.example.hanaro.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "상품 API", description = "상품 관련 API")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@Operation(summary = "상품 등록 (관리자)", description = "새로운 상품을 등록합니다.")
	@SecurityRequirement(name = "JWT Authentication")
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BaseResponse<Void>> createProduct(@Valid @ModelAttribute ProductCreateRequestDto requestDto) {
		productService.createProduct(requestDto);
		return ResponseEntity.ok(new BaseResponse<>());
	}

	@Operation(summary = "상품 목록 조회 (공용)", description = "모든 사용자가 상품 목록을 조회합니다.")
	@GetMapping
	public ResponseEntity<BaseResponse<List<ProductDto>>> getProducts(Authentication authentication) {
		List<ProductDto> products = productService.findProducts(authentication);
		return ResponseEntity.ok(new BaseResponse<>(products));
	}
}