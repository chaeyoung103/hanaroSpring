package com.example.hanaro.domain.user.controller;

import com.example.hanaro.domain.user.dto.request.UserSignInRequestDto;
import com.example.hanaro.domain.user.dto.request.UserSignUpRequestDto;
import com.example.hanaro.domain.user.dto.response.UserSignInResponseDto;
import com.example.hanaro.domain.user.service.UserService;
import com.example.hanaro.global.response.BaseResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	// 회원가입 API
	@PostMapping("/signup")
	public ResponseEntity<BaseResponse<Void>> signUp(@Valid @RequestBody UserSignUpRequestDto requestDto) { // <-- @Valid 추가!
		userService.signUp(requestDto);
		return ResponseEntity.ok(new BaseResponse<>());
	}

	// 로그인 API
	@PostMapping("/signin")
	public ResponseEntity<BaseResponse<UserSignInResponseDto>> signIn(
		@Valid @RequestBody UserSignInRequestDto requestDto) {
		UserSignInResponseDto response = userService.signIn(requestDto);
		return ResponseEntity.ok(new BaseResponse<>(response));
	}
}