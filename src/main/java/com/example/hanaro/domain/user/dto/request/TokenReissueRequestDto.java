package com.example.hanaro.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // JSON 역직렬화를 위해 기본 생성자 추가
public class TokenReissueRequestDto {
	private String refreshToken;
}