package com.example.hanaro.domain.user.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserSignInRequestDto {

	@NotBlank(message = "이메일은 필수 입력 값입니다.")
	@Email(message = "이메일 형식이 올바르지 않습니다.")
	@Schema(name = "email", example = "user1@example.com")
	private String email;

	@NotBlank(message = "비밀번호는 필수 입력 값입니다.")
	@Schema(name = "password", example = "password1234")
	private String password;
}