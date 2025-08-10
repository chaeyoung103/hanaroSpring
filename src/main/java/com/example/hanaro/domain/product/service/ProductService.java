package com.example.hanaro.domain.product.service;

import com.example.hanaro.domain.product.dto.request.ProductCreateRequestDto;
import com.example.hanaro.domain.product.dto.response.ProductDto;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ProductService {

	/**
	 * 상품 등록 (관리자용)
	 * @param requestDto 상품 생성 요청 DTO
	 */
	void createProduct(ProductCreateRequestDto requestDto);

	/**
	 * 상품 목록 조회 (사용자/관리자 공용)
	 * @param authentication 현재 사용자 인증 정보
	 * @return 상품 DTO 리스트
	 */
	List<ProductDto> findProducts(Authentication authentication);
}