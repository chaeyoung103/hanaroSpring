package com.example.hanaro.domain.user.dto.request;
import lombok.Getter;

@Getter
public class UserSignInRequestDto {
	private String email;
	private String password;
}