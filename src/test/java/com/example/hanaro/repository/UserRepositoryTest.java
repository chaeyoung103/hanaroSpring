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

	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
		User user = User.builder()
			.email("test@example.com")
			.password(passwordEncoder.encode("password1234"))
			.nickname("tester")
			.role("ROLE_USER")
			.refreshToken("dummy-refresh-token")
			.build();
		userRepository.save(user);
	}

	@Test
	@DisplayName("이메일로 사용자를 성공적으로 조회한다")
	void findByEmail_Success() {
		// when
		Optional<User> foundUser = userRepository.findByEmail("test@example.com");
		// then
		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getNickname()).isEqualTo("tester");
	}

	@Test
	@DisplayName("닉네임으로 사용자를 성공적으로 조회한다")
	void findByNickname_Success() {
		// when
		Optional<User> foundUser = userRepository.findByNickname("tester");
		// then
		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
	}

	@Test
	@DisplayName("리프레시 토큰으로 사용자를 성공적으로 조회한다")
	void findByRefreshToken_Success() {
		// when
		Optional<User> foundUser = userRepository.findByRefreshToken("dummy-refresh-token");
		// then
		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
	}



}