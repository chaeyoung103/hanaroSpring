package com.example.hanaro.global.jwt;

import com.example.hanaro.global.jwt.exception.CustomJwtException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	// @Override
	// protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException { ... }

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String authorizationHeader = request.getHeader("Authorization");

		// 1. Authorization 헤더가 없거나 'Bearer '로 시작하지 않으면 통과
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		// 2. 'Bearer ' 부분을 제거하여 순수 토큰만 추출
		String token = authorizationHeader.substring(7);

		try {
			// 3. 토큰 검증 및 Claims 추출
			Claims claims = jwtUtil.validateToken(token);

			// 4. Claims에서 사용자 정보 추출
			String email = claims.get("email", String.class);
			String role = claims.get("role", String.class);

			// 5. Spring Security가 이해할 수 있는 Authentication 객체 생성
			Authentication authentication = new UsernamePasswordAuthenticationToken(
				email, // Principal (주로 사용자 ID, 이메일 등)
				null,  // Credentials (비밀번호, JWT에서는 사용 안 함)
				Collections.singletonList(new SimpleGrantedAuthority(role)) // Authorities (권한 목록)
			);

			// 6. SecurityContext에 인증 정보 저장
			SecurityContextHolder.getContext().setAuthentication(authentication);

		} catch (CustomJwtException e) {
			// 토큰 관련 예외 발생 시 SecurityContext를 비움 (인증되지 않은 상태로 유지)
			SecurityContextHolder.clearContext();
			log.error("JWT Error: {}", e.getMessage());
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized 응답
		}

		// 7. 다음 필터로 요청 전달
		filterChain.doFilter(request, response);
	}
}