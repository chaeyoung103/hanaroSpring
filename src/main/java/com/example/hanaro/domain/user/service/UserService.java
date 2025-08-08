package com.example.hanaro.domain.user.service;

import com.example.hanaro.domain.user.dto.request.UserSignInRequestDto;
import com.example.hanaro.domain.user.dto.request.UserSignUpRequestDto;

public interface UserService {

	/**
	 * 회원가입
	 * @param requestDto 회원가입 요청 정보
	 */
	void signUp(UserSignUpRequestDto requestDto);

	/**
	 * 로그인
	 * @param requestDto 로그인 요청 정보
	 * @return JWT 토큰
	 */
	String signIn(UserSignInRequestDto requestDto);
}