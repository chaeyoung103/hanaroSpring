package com.example.hanaro.global.exception;

import com.example.hanaro.global.response.ErrorCode;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
	private final ErrorCode exceptionStatus;

	public BaseException(ErrorCode exceptionStatus) {
		super(exceptionStatus.getMessage());
		this.exceptionStatus = exceptionStatus;
	}

	public BaseException(ErrorCode exceptionStatus, String customMessage) {
		super(exceptionStatus.getMessage() + " (" + customMessage + ")");
		this.exceptionStatus = exceptionStatus;
	}
}