package com.example.hanaro.domain.user.exception;

import com.example.hanaro.global.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

	// 기존 에러 코드
	USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "U001", "존재하지 않는 회원입니다."),
	DUPLICATE_EMAIL(HttpStatus.CONFLICT.value(), "U002", "이미 사용중인 이메일입니다."),
	DUPLICATE_NICKNAME(HttpStatus.CONFLICT.value(), "U003", "이미 사용중인 닉네임입니다."),
	INVALID_PASSWORD(HttpStatus.UNAUTHORIZED.value(), "U004", "비밀번호가 일치하지 않습니다."),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(), "U005", "유효하지 않은 토큰입니다.");

	private final int status;
	private final String code;
	private final String message;
}