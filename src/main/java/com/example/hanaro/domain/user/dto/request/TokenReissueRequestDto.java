package com.example.hanaro.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenReissueRequestDto {
	private String refreshToken;
}