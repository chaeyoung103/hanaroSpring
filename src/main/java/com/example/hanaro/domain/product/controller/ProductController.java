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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "상품 API", description = "상품 관련 API")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

	private final ProductService productService;

	@Operation(summary = "상품 등록 (관리자)", description = "새로운 상품을 등록합니다.")
	@SecurityRequirement(name = "JWT Authentication")
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BaseResponse<Void>> createProduct(@Valid @ModelAttribute ProductCreateRequestDto requestDto) {
		log.info("Received request to create product: {}", requestDto.getName());
		if (requestDto.getImages() != null) {
			log.info("Number of images received in controller: {}", requestDto.getImages().size());
		} else {
			log.info("No images received in controller.");
		}
		productService.createProduct(requestDto);
		return ResponseEntity.ok(new BaseResponse<>());
	}

	@Operation(summary = "상품 목록 전체 조회 (관리자용)", description = "시스템의 모든 상품 목록을 조회합니다.")
	@SecurityRequirement(name = "JWT Authentication")
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BaseResponse<List<ProductDto>>> getAllProducts() {
		List<ProductDto> products = productService.findAllProducts();
		return ResponseEntity.ok(new BaseResponse<>(products));
	}

	@Operation(summary = "상품 목록 검색 (사용자용)", description = "키워드로 상품을 검색합니다. 키워드가 없으면 전체 목록이 조회됩니다.")
	@GetMapping("/search")
	public ResponseEntity<BaseResponse<List<ProductDto>>> searchProducts(
		@RequestParam(required = false) String keyword) {
		List<ProductDto> products = productService.searchProducts(keyword);
		return ResponseEntity.ok(new BaseResponse<>(products));
	}
}