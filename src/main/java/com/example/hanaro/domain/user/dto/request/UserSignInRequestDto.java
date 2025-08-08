package com.example.hanaro.domain.user.dto.request;
import lombok.Getter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserSignInRequestDto {

	@NotBlank(message = "이메일은 필수 입력 값입니다.")
	@Email(message = "이메일 형식이 올바르지 않습니다.") // <-- [추가] 이메일 형식 검사
	private String email;

	@NotBlank(message = "비밀번호는 필수 입력 값입니다.") // <-- [추가] 빈 값 검사
	private String password;
}