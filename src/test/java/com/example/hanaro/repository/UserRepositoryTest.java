package com.example.hanaro.domain.user.repository;

import com.example.hanaro.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@Import(UserRepositoryTest.TestConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

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

	@Test
	@DisplayName("관리자 계정 생성")
	@Rollback(false) // DB에 변경사항을 실제로 반영 (롤백 안함)
	void createAdminUser() {
		String adminEmail = "hanaro";
		String adminPassword = "12345678";
		String adminNickname = "admin";
		String adminRole = "ROLE_ADMIN";

		// 이미 존재하는 계정인지 확인 (테스트를 여러 번 실행할 경우를 대비)
		if (userRepository.findByEmail(adminEmail).isEmpty()) {
			User adminUser = new User();
			adminUser.setEmail(adminEmail);
			adminUser.setPassword(passwordEncoder.encode(adminPassword));
			adminUser.setNickname(adminNickname);
			adminUser.setRole(adminRole);

			userRepository.save(adminUser);
		}
	}

	/**
	 * 일반 사용자 더미 데이터 10개를 생성
	 */
	@Test
	@DisplayName("일반 사용자 더미 데이터 10명 생성")
	@Rollback(false)
	void createDummyUsers() {
		for (int i = 1; i <= 10; i++) {
			String userEmail = "user" + i + "@example.com";

			// 이메일이 이미 존재하지 않을 경우에만 데이터 생성
			if (userRepository.findByEmail(userEmail).isEmpty()) {
				User user = new User();
				user.setEmail(userEmail);
				user.setPassword(passwordEncoder.encode("password1234"));
				user.setNickname("user" + i);
				user.setRole("ROLE_USER");

				userRepository.save(user);
			}
		}
	}
}