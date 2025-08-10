package com.example.hanaro.domain.user.dto.response;

import com.example.hanaro.domain.user.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {
	private final Long userId;
	private final String email;
	private final String nickname;
	private final String role;
}