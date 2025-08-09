package com.example.hanaro.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserSignInResponseDto {
	private final String accessToken;
	private final String refreshToken;
	private final String nickname;
	private final String role;

	public UserSignInResponseDto(String accessToken, String refreshToken, String nickname, String role) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.nickname = nickname;
		this.role = role;
	}
}