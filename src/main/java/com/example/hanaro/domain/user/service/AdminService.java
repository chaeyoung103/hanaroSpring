package com.example.hanaro.domain.user.service;


import com.example.hanaro.domain.user.dto.request.AdminSignInRequestDto;
import com.example.hanaro.domain.user.dto.response.UserDto;
import com.example.hanaro.domain.user.dto.response.UserSignInResponseDto;

import java.util.List;

public interface AdminService {

	/**
	 * 관리자 로그인
	 * @param requestDto 관리자 로그인 요청 정보
	 * @return JWT 토큰 DTO
	 */
	UserSignInResponseDto signIn(AdminSignInRequestDto requestDto);

	/**
	 * 전체 회원 목록 조회
	 * @return 전체 회원 정보 DTO 리스트
	 */
	List<UserDto> findUsers();

	/**
	 * 특정 회원 삭제
	 * @param userId 삭제할 회원의 ID
	 */
	void deleteUser(Long userId);
}