package com.example.hanaro.domain.user.service;

import com.example.hanaro.domain.user.dto.request.UserSignInRequestDto;
import com.example.hanaro.domain.user.dto.request.UserSignUpRequestDto;
import com.example.hanaro.domain.user.entity.User;
import com.example.hanaro.domain.user.repository.UserRepository;
import com.example.hanaro.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // final 필드를 위한 생성자 자동 생성
public class UserServiceImpl implements UserService { // UserService 인터페이스를 '구현(implements)'

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	// 회원가입
	@Override // 인터페이스의 메서드를 구현한다는 의미
	@Transactional
	public void signUp(UserSignUpRequestDto requestDto) {
		// 이메일 중복 확인
		if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
			throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
		}
		// 닉네임 중복 확인
		if (userRepository.findByNickname(requestDto.getNickname()).isPresent()) {
			throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
		}

		User user = new User();
		user.setEmail(requestDto.getEmail());
		user.setPassword(passwordEncoder.encode(requestDto.getPassword())); // 비밀번호 암호화
		user.setNickname(requestDto.getNickname());
		user.setRole("ROLE_USER"); // 기본 역할은 USER

		userRepository.save(user);
	}

	// 로그인
	@Override // 인터페이스의 메서드를 구현한다는 의미
	public String signIn(UserSignInRequestDto requestDto) {
		User user = userRepository.findByEmail(requestDto.getEmail())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

		// 비밀번호 확인
		if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}

		// JWT 토큰 생성 및 반환
		return jwtUtil.createToken(user.getEmail(), user.getRole());
	}
}