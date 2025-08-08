package com.example.hanaro.domain.user.exception;

import com.example.hanaro.global.response.ErrorCode;
import lombok.Getter;

@Getter
public class UserException extends RuntimeException {

	private final ErrorCode errorCode;

	public UserException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}