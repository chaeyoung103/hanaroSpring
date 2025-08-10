package com.example.hanaro.domain.user.exception;


import com.example.hanaro.global.response.BaseErrorResponse;
import com.example.hanaro.global.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler {

	/**
	 * UserException 처리
	 * @param e UserException
	 * @return ResponseEntity<BaseErrorResponse>
	 */
	@ExceptionHandler(UserException.class)
	public ResponseEntity<BaseErrorResponse> handleUserException(UserException e) {
		ErrorCode errorCode = e.getErrorCode();
		log.error("UserException occurred: {}", errorCode.getMessage(), e);

		BaseErrorResponse response = new BaseErrorResponse(errorCode);

		return ResponseEntity
			.status(errorCode.getStatus())
			.body(response);
	}
}