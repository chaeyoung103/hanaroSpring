package com.example.hanaro.domain.user.dto.request;


import lombok.Getter;

@Getter
public class UserSignUpRequestDto {
	private String email;
	private String password;
	private String nickname;
}