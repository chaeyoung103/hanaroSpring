package com.example.hanaro.domain.user.service;

import com.example.hanaro.domain.user.dto.request.UserSignInRequestDto;
import com.example.hanaro.domain.user.dto.request.UserSignUpRequestDto;
import com.example.hanaro.domain.user.dto.response.UserSignInResponseDto;
import com.example.hanaro.domain.user.entity.User;
import com.example.hanaro.domain.user.exception.UserException;
import com.example.hanaro.domain.user.repository.UserRepository;
import com.example.hanaro.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.hanaro.domain.user.exception.UserErrorCode.*;

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
		if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
			throw new UserException(DUPLICATE_EMAIL);
		}
		if (userRepository.findByNickname(requestDto.getNickname()).isPresent()) {
			throw new UserException(DUPLICATE_NICKNAME);
		}

		User user = new User();
		user.setEmail(requestDto.getEmail());
		user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
		user.setNickname(requestDto.getNickname());
		user.setRole("ROLE_USER");

		userRepository.save(user);
	}

	// 로그인
	@Override
	public UserSignInResponseDto signIn(UserSignInRequestDto requestDto) { // [수정] 반환 타입 String -> UserSignInResponse
		User user = userRepository.findByEmail(requestDto.getEmail())
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));

		if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
			throw new UserException(INVALID_PASSWORD);
		}

		// JWT 토큰 생성
		String token = jwtUtil.createAccessToken(user.getEmail(), user.getRole());

		return new UserSignInResponseDto(token, user.getNickname(), user.getRole());
	}
}