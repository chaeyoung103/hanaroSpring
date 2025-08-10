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
class UserRepositoryTest {

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
	@DisplayName("관리자 계정 생성 테스트")
	@Rollback(false)
	void createAdminUser() {
		String adminEmail = "hanaro";
		String adminPassword = "12345678";
		String adminNickname = "admin";
		String adminRole = "ROLE_ADMIN";

		User adminUser = new User();
		adminUser.setEmail(adminEmail);
		adminUser.setPassword(passwordEncoder.encode(adminPassword));
		adminUser.setNickname(adminNickname);
		adminUser.setRole(adminRole);

		User savedAdmin = userRepository.save(adminUser);

		assertThat(savedAdmin).isNotNull();
		assertThat(savedAdmin.getId()).isNotNull();
		assertThat(savedAdmin.getEmail()).isEqualTo(adminEmail);
		assertThat(savedAdmin.getRole()).isEqualTo(adminRole);
		assertThat(passwordEncoder.matches(adminPassword, savedAdmin.getPassword())).isTrue();
	}
}