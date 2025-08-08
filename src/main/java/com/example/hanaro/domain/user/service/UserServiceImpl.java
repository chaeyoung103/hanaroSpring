package com.example.hanaro.domain.user.service;

import com.example.hanaro.domain.user.dto.request.UserSignInRequestDto;
import com.example.hanaro.domain.user.dto.request.UserSignUpRequestDto;
import com.example.hanaro.domain.user.entity.User;
import com.example.hanaro.domain.user.exception.UserException;
import com.example.hanaro.domain.user.repository.UserRepository;
import com.example.hanaro.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.hanaro.domain.user.exception.UserErrorCode.*; // UserErrorCode import

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	// 회원가입
	@Override
	@Transactional
	public void signUp(UserSignUpRequestDto requestDto) {
		// 이메일 중복 확인
		if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
			throw new UserException(DUPLICATE_EMAIL);
		}
		// 닉네임 중복 확인
		if (userRepository.findByNickname(requestDto.getNickname()).isPresent()) {
			throw new UserException(DUPLICATE_NICKNAME);
		}

		User user = new User();
		user.setEmail(requestDto.getEmail());
		user.setPassword(passwordEncoder.encode(requestDto.getPassword())); // 비밀번호 암호화
		user.setNickname(requestDto.getNickname());
		user.setRole("ROLE_USER"); // 기본 역할은 USER

		userRepository.save(user);
	}

	// 로그인
	@Override
	public String signIn(UserSignInRequestDto requestDto) {
		User user = userRepository.findByEmail(requestDto.getEmail())
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));

		// 비밀번호 확인
		if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
			// [수정] IllegalArgumentException -> UserException
			throw new UserException(INVALID_PASSWORD);
		}

		// JWT 토큰 생성 및 반환
		return jwtUtil.createToken(user.getEmail(), user.getRole());
	}
}