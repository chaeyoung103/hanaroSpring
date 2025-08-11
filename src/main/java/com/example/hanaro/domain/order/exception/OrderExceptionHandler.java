package com.example.hanaro.domain.order.exception;

import com.example.hanaro.global.response.BaseErrorResponse;
import com.example.hanaro.global.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class OrderExceptionHandler {

	@ExceptionHandler(OrderException.class)
	public ResponseEntity<BaseErrorResponse> handleOrderException(OrderException e) {
		ErrorCode errorCode = e.getErrorCode();
		String message = e.getMessage();

		log.error("OrderException occurred: {}", message, e);

		BaseErrorResponse response = new BaseErrorResponse(errorCode, message);

		return ResponseEntity
			.status(errorCode.getStatus())
			.body(response);
	}
}