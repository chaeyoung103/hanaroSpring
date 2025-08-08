package com.example.hanaro.global.response;

public interface ErrorCode {
	int getStatus();
	String getCode();
	String getMessage();
}