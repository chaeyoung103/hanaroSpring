package com.example.hanaro.global.response;



import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum BaseErrorCode implements ErrorCode {
	SUCCESS(HttpStatus.OK.value(), "B001", "요청에 성공하였습니다."),
	ARGUMENT_TYPE_MISMATCH(HttpStatus.BAD_REQUEST.value(), "B002", "잘못된 파라미터 타입입니다."),
	IMAGE_NOT_PRESENT(HttpStatus.BAD_REQUEST.value(), "B003", "Request part 이미지가 존재하지 않습니다."),
	ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "B004", "접근 권한이 없습니다."), // <-- [수정] 세미콜론(;)을 콤마(,)로 변경
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(), "B005", "유효하지 않은 토큰입니다.");

	private final int status;
	private final String code;
	private final String message;
}