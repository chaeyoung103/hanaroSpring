package com.example.hanaro.domain.product.service;

import com.example.hanaro.domain.product.dto.request.ProductCreateRequestDto;
import com.example.hanaro.domain.product.dto.request.ProductStockUpdateRequestDto;
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
	 * 상품 목록 전체 조회 (관리자용)
	 * @return 전체 상품 DTO 리스트
	 */
	List<ProductDto> findAllProducts();

	/**
	 * 상품 목록 검색 (사용자용)
	 * @param keyword 검색할 키워드
	 * @return 검색된 상품 DTO 리스트
	 */
	List<ProductDto> searchProducts(String keyword);

	/**
	 * 상품 정보 수정 (관리자용)
	 * @param productId 수정할 상품의 ID
	 * @param requestDto 수정할 상품 정보 DTO
	 */
	void updateProduct(Long productId, ProductCreateRequestDto requestDto);

	/**
	 * 상품 재고 수정 (관리자용)
	 * @param productId 수정할 상품의 ID
	 * @param requestDto 새로운 재고 정보 DTO
	 */
	void updateStock(Long productId, ProductStockUpdateRequestDto requestDto);

	/**
	 * 상품 삭제 (관리자용)
	 * @param productId 삭제할 상품의 ID
	 */
	void deleteProduct(Long productId);

	/**
	 * 상품 상세 조회 (사용자용)
	 * @param productId 조회할 상품의 ID
	 * @return 상품 상세 정보 DTO
	 */
	ProductDto findProductById(Long productId);
}