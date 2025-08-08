package com.example.hanaro.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserSignInResponseDto {
	private final String token;
	private final String nickname;
	private final String role;

	public UserSignInResponseDto(String token, String nickname, String role) {
		this.token = token;
		this.nickname = nickname;
		this.role = role;
	}
}