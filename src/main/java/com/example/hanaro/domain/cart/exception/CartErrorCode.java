package com.example.hanaro.domain.cart.exception;

import com.example.hanaro.global.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartErrorCode implements ErrorCode {

	CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "C001", "해당 장바구니 아이템을 찾을 수 없습니다."),
	ACCESS_DENIED_CART_ITEM(HttpStatus.FORBIDDEN.value(), "C002", "해당 장바구니 아이템에 대한 접근 권한이 없습니다."),
	INSUFFICIENT_STOCK(HttpStatus.CONFLICT.value(), "C003", "상품의 재고가 부족합니다.");

	private final int status;
	private final String code;
	private final String message;
}