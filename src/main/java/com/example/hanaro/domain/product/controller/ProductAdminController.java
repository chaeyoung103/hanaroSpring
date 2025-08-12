package com.example.hanaro.domain.product.controller;

import com.example.hanaro.domain.product.dto.request.ProductCreateRequestDto;
import com.example.hanaro.domain.product.dto.request.ProductStockUpdateRequestDto;
import com.example.hanaro.domain.product.dto.response.ProductDto;
import com.example.hanaro.domain.product.service.ProductService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "[관리자] 상품", description = "상품 관련 API (관리자용)")
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@Slf4j
public class ProductAdminController {

	private final ProductService productService;

	@Operation(summary = "상품 등록 (관리자)", description = "새로운 상품을 등록합니다.")
	@SecurityRequirement(name = "JWT Authentication")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "상품 등록 성공"),
		@ApiResponse(responseCode = "409", description = "이미 존재하는 상품명", content = @Content(schema = @Schema(implementation = BaseErrorResponse.class))),
		@ApiResponse(responseCode = "403", description = "접근 권한 없음 (관리자만 가능)", content = @Content(schema = @Schema(implementation = BaseErrorResponse.class)))
	})
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BaseResponse<Void>> createProduct(@Valid @ModelAttribute ProductCreateRequestDto requestDto) {
		log.info("상품 등록 요청 수신: {}", requestDto.getName());
		if (requestDto.getImages() != null) {
			log.info("수신된 이미지 개수: {}", requestDto.getImages().size());
		} else {
			log.info("수신된 이미지 없음.");
		}
		productService.createProduct(requestDto);
		return ResponseEntity.ok(new BaseResponse<>());
	}

	@Operation(summary = "상품 목록 전체 조회 (관리자용)", description = "시스템의 모든 상품 목록을 조회합니다.")
	@SecurityRequirement(name = "JWT Authentication")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "상품 목록 조회 성공"),
		@ApiResponse(responseCode = "403", description = "접근 권한 없음 (관리자만 가능)", content = @Content(schema = @Schema(implementation = BaseErrorResponse.class)))
	})
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BaseResponse<List<ProductDto>>> getAllProducts() {
		List<ProductDto> products = productService.findAllProducts();
		return ResponseEntity.ok(new BaseResponse<>(products));
	}

	@Operation(summary = "상품 수정 (관리자용)", description = "기존 상품의 정보를 수정합니다. 이미지도 교체 가능합니다.")
	@SecurityRequirement(name = "JWT Authentication")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "상품 수정 성공"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 상품", content = @Content(schema = @Schema(implementation = BaseErrorResponse.class))),
		@ApiResponse(responseCode = "403", description = "접근 권한 없음 (관리자만 가능)", content = @Content(schema = @Schema(implementation = BaseErrorResponse.class)))
	})
	@PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BaseResponse<Void>> updateProduct(
		@PathVariable Long productId,
		@Valid @ModelAttribute ProductCreateRequestDto requestDto) {
		productService.updateProduct(productId, requestDto);
		return ResponseEntity.ok(new BaseResponse<>());
	}

	@Operation(summary = "상품 재고 수정 (관리자용)", description = "특정 상품의 재고 수량을 조절합니다.")
	@SecurityRequirement(name = "JWT Authentication")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "재고 수정 성공"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 상품", content = @Content(schema = @Schema(implementation = BaseErrorResponse.class))),
		@ApiResponse(responseCode = "403", description = "접근 권한 없음 (관리자만 가능)", content = @Content(schema = @Schema(implementation = BaseErrorResponse.class)))
	})
	@PatchMapping("/{productId}/stock")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BaseResponse<Void>> updateStock(
		@PathVariable Long productId,
		@Valid @RequestBody ProductStockUpdateRequestDto requestDto) {
		productService.updateStock(productId, requestDto);
		return ResponseEntity.ok(new BaseResponse<>());
	}

	@Operation(summary = "상품 삭제 (관리자용)", description = "상품을 시스템에서 삭제합니다.")
	@SecurityRequirement(name = "JWT Authentication")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "상품 삭제 성공"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 상품", content = @Content(schema = @Schema(implementation = BaseErrorResponse.class))),
		@ApiResponse(responseCode = "409", description = "주문 내역이 있어 삭제할 수 없는 상품", content = @Content(schema = @Schema(implementation = BaseErrorResponse.class))),
		@ApiResponse(responseCode = "403", description = "접근 권한 없음 (관리자만 가능)", content = @Content(schema = @Schema(implementation = BaseErrorResponse.class)))
	})
	@DeleteMapping("/{productId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BaseResponse<Void>> deleteProduct(@PathVariable Long productId) {
		productService.deleteProduct(productId);
		return ResponseEntity.ok(new BaseResponse<>());
	}
}