package com.example.hanaro.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AdminSignInRequestDto {

	@NotBlank(message = "아이디를 입력해주세요.")
	private String email; // 필드명은 email을 그대로 사용

	@NotBlank(message = "비밀번호를 입력해주세요.")
	private String password;
}