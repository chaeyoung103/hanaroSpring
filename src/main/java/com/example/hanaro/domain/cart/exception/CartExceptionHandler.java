package com.example.hanaro.domain.cart.exception;

import com.example.hanaro.global.response.BaseErrorResponse;
import com.example.hanaro.global.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CartExceptionHandler {

	@ExceptionHandler(CartException.class)
	public ResponseEntity<BaseErrorResponse> handleCartException(CartException e) {
		ErrorCode errorCode = e.getErrorCode();
		log.error("CartException occurred: {}", errorCode.getMessage(), e);

		BaseErrorResponse response = new BaseErrorResponse(errorCode);

		return ResponseEntity
			.status(errorCode.getStatus())
			.body(response);
	}
}