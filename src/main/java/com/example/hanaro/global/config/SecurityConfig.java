package com.example.hanaro.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	// 비밀번호 암호화
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Spring Security의 필터 체인
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			// CSRF(Cross-Site Request Forgery) 보호 기능 비활성화
			.csrf(AbstractHttpConfigurer::disable)

			// JWT를 사용하므로 세션 관리는 STATELESS(상태 없음)로 설정
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

			// HTTP 요청에 대한 접근 권한 설정
			.authorizeHttpRequests(auth -> auth
				// '/users/signup', '/users/signin' API는 누구나 접근 가능
				.requestMatchers("/users/signup", "/users/signin").permitAll()
				// 그 외의 모든 API는 인증된 사용자만 접근 가능
				.anyRequest().authenticated()
			);

		// TODO: 다음 단계에서 만들 JWT 인증 필터를 여기에 추가할 예정입니다.

		return http.build();
	}
}