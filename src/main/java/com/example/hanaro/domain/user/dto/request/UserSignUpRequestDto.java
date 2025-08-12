package com.example.hanaro.domain.user.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserSignUpRequestDto {

	@NotBlank(message = "이메일은 필수 입력 값입니다.")
	@Email(message = "이메일 형식이 올바르지 않습니다.")
	@Schema(name = "email", example = "user1@example.com")

	private String email;

	@NotBlank(message = "비밀번호는 필수 입력 값입니다.")
	@Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
	@Schema(name = "password", example = "password1234")
	private String password;

	@NotBlank(message = "닉네임은 필수 입력 값입니다.")
	@Size(min = 1, max = 30, message = "닉네임은 1자 이상 30자 이하이어야 합니다.")
	@Schema(name = "nickname", example = "userNickname")
	private String nickname;
}