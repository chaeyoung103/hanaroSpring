package com.example.hanaro.repository;

import com.example.hanaro.domain.user.entity.User;
import com.example.hanaro.domain.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@Import(UserRepositoryTest.TestConfig.class)
public class UserRepositoryTest extends RepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	static class TestConfig {
		@Bean
		public PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}
	}

	private User testUser;

	// 각 테스트가 실행되기 전에 테스트용 유저를 미리 저장
	@BeforeEach
	void setUp() {
		userRepository.deleteAll();

		testUser = new User();
		testUser.setEmail("testuser@example.com");
		testUser.setPassword(passwordEncoder.encode("password1234"));
		testUser.setNickname("testuser");
		testUser.setRole("ROLE_USER");
		userRepository.save(testUser);
	}


	@Test
	@DisplayName("이메일로 사용자 조회 성공")
	void findByEmail_Success() {
		Optional<User> foundUser = userRepository.findByEmail("testuser@example.com");

		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getNickname()).isEqualTo("testuser");
	}

	@Test
	@DisplayName("닉네임으로 사용자 조회 성공")
	void findByNickname_Success() {
		// when
		Optional<User> foundUser = userRepository.findByNickname("testuser");

		// then
		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getEmail()).isEqualTo("testuser@example.com");
	}

}