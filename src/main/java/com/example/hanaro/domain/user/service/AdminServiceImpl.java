package com.example.hanaro.domain.user.service;


import com.example.hanaro.domain.user.dto.request.AdminSignInRequestDto;
import com.example.hanaro.domain.user.dto.response.UserDto;
import com.example.hanaro.domain.user.dto.response.UserSignInResponseDto;
import com.example.hanaro.domain.user.entity.User;
import com.example.hanaro.domain.user.exception.UserException;
import com.example.hanaro.domain.user.repository.UserRepository;
import com.example.hanaro.global.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.hanaro.domain.user.exception.UserErrorCode.*;
import static com.example.hanaro.global.response.BaseErrorCode.*;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Override
	@Transactional
	public UserSignInResponseDto signIn(AdminSignInRequestDto requestDto) {
		User user = userRepository.findByEmail(requestDto.getEmail())
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));

		if (!"ROLE_ADMIN".equals(user.getRole())) {
			throw new UserException(ACCESS_DENIED);
		}

		if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
			throw new UserException(INVALID_PASSWORD);
		}

		String accessToken = jwtUtil.createAccessToken(user.getEmail(), user.getRole());
		String refreshToken = jwtUtil.createRefreshToken();
		user.updateRefreshToken(refreshToken);
		userRepository.save(user);

		return UserSignInResponseDto.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.nickname(user.getNickname())
			.role(user.getRole())
			.build();
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDto> findUsers() {
		return userRepository.findAll().stream()
			.map(user -> UserDto.builder()
				.userId(user.getId())
				.email(user.getEmail())
				.nickname(user.getNickname())
				.role(user.getRole())
				.build())
			.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deleteUser(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));

		userRepository.delete(user);
	}
}