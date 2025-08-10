package com.example.hanaro.domain.product.exception;

import com.example.hanaro.global.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {

	DUPLICATE_PRODUCT_NAME(HttpStatus.CONFLICT.value(), "P001", "이미 존재하는 상품명입니다."),
	PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "P002", "존재하지 않는 상품입니다."),
	FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "P003", "이미지 업로드에 실패했습니다.");

	private final int status;
	private final String code;
	private final String message;
}