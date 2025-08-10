package com.example.hanaro.domain.product.exception;

import com.example.hanaro.global.response.ErrorCode;
import lombok.Getter;

@Getter
public class ProductException extends RuntimeException {

	private final ErrorCode errorCode;

	public ProductException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}