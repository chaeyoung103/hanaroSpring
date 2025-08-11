package com.example.hanaro.domain.order.exception;

import com.example.hanaro.global.response.ErrorCode;
import lombok.Getter;

@Getter
public class OrderException extends RuntimeException {

	private final ErrorCode errorCode;

	public OrderException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	// 재고 부족 시, 어떤 상품이 문제인지 메시지에 포함시키기 위한 생성자
	public OrderException(ErrorCode errorCode, String productName) {
		super(errorCode.getMessage() + " : " + productName);
		this.errorCode = errorCode;
	}
}