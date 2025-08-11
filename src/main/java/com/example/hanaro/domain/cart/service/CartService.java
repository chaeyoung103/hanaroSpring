package com.example.hanaro.domain.cart.service;

import com.example.hanaro.domain.cart.dto.request.CartItemRequestDto;
import com.example.hanaro.domain.cart.dto.request.CartItemUpdateRequestDto;
import com.example.hanaro.domain.cart.dto.response.CartResponseDto;

public interface CartService {
	/**
	 * 장바구니에 상품 추가
	 * @param userId 현재 로그인한 사용자의 ID
	 * @param requestDto 추가할 상품 정보 (productId, quantity)
	 */
	void addProductToCart(Long userId, CartItemRequestDto requestDto);

	/**
	 * 내 장바구니 조회
	 * @param userId 현재 로그인한 사용자의 ID
	 * @return 장바구니에 담긴 상품 목록 및 총액
	 */
	CartResponseDto getCart(Long userId);

	/**
	 * 장바구니 상품 수량 변경
	 * @param userId 현재 로그인한 사용자의 ID
	 * @param cartItemId 수정할 장바구니 상품의 ID
	 * @param requestDto 변경할 수량 정보
	 */
	void updateCartItemQuantity(Long userId, Long cartItemId, CartItemUpdateRequestDto requestDto);

	/**
	 * 장바구니 상품 삭제
	 * @param userId 현재 로그인한 사용자의 ID
	 * @param cartItemId 삭제할 장바구니 상품의 ID
	 */
	void deleteCartItem(Long userId, Long cartItemId);
}