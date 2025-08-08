package com.example.hanaro.domain.user.controller;

import com.example.hanaro.domain.user.dto.request.UserSignInRequestDto;
import com.example.hanaro.domain.user.dto.request.UserSignUpRequestDto;
import com.example.hanaro.domain.user.service.UserService;
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
	public ResponseEntity<String> signUp(@RequestBody UserSignUpRequestDto requestDto) {
		userService.signUp(requestDto);
		return ResponseEntity.ok("회원가입 성공");
	}

	// 로그인 API
	@PostMapping("/signin")
	public ResponseEntity<String> signIn(@RequestBody UserSignInRequestDto requestDto) {
		String token = userService.signIn(requestDto);
		return ResponseEntity.ok(token);
	}
}