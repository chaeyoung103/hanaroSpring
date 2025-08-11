package com.example.hanaro.domain.order.exception;


import com.example.hanaro.global.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {

	CART_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "O001", "장바구니를 찾을 수 없습니다."),
	CART_IS_EMPTY(HttpStatus.BAD_REQUEST.value(), "O002", "장바구니가 비어있습니다."),
	INSUFFICIENT_STOCK(HttpStatus.CONFLICT.value(), "O003", "상품의 재고가 부족합니다.");

	private final int status;
	private final String code;
	private final String message;
}