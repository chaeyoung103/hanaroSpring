package com.example.hanaro.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AdminSignInRequestDto {

	@NotBlank(message = "아이디를 입력해주세요.")
	@Schema(name = "email", example = "hanaro")
	private String email;

	@NotBlank(message = "비밀번호를 입력해주세요.")
	@Schema(name = "password", example = "12345678")
	private String password;
}