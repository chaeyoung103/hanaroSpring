package com.example.hanaro.domain.user.service;

import com.example.hanaro.domain.user.dto.request.UserSignInRequestDto;
import com.example.hanaro.domain.user.dto.request.UserSignUpRequestDto;
import com.example.hanaro.domain.user.dto.response.UserSignInResponseDto;

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
	UserSignInResponseDto signIn(UserSignInRequestDto requestDto);

	/**
	 * 리프레시 토큰을 사용하여 액세스 토큰 갱신
	 * @param refreshToken 리프레시 토큰
	 * @return 새로운 액세스 토큰, 리프레시 토큰
	 */
	UserSignInResponseDto reissueToken(String refreshToken);
}