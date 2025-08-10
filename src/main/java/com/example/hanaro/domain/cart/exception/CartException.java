package com.example.hanaro.domain.cart.exception;

import com.example.hanaro.global.response.ErrorCode;
import lombok.Getter;

@Getter
public class CartException extends RuntimeException {

	private final ErrorCode errorCode;

	public CartException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}