package com.example.hanaro.global.jwt;

import com.example.hanaro.global.jwt.exception.CustomJwtException;
import com.example.hanaro.global.response.BaseErrorCode;
import com.example.hanaro.global.response.BaseErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.example.hanaro.global.response.BaseErrorCode.INVALID_TOKEN;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final ObjectMapper objectMapper = new ObjectMapper();

	private static final List<String> PERMIT_ALL_URLS = Arrays.asList(
		"/api/users/signup",
		"/api/users/signin",
		"/api/admin/signin",
		"/api/users/reissue"
	);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String path = request.getRequestURI();
		if (PERMIT_ALL_URLS.contains(path)) {
			filterChain.doFilter(request, response);
			return;
		}

		String authorizationHeader = request.getHeader("Authorization");

		// Authorization 헤더가 없거나 Bearer로 시작하지 않는 경우
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		String token = authorizationHeader.substring(7);
		// JWT 예외 직접 처리
		try {
			Claims claims = jwtUtil.validateToken(token);
			String email = claims.get("email", String.class);
			String role = claims.get("role", String.class);
			Authentication authentication = new UsernamePasswordAuthenticationToken(
				email,
				null,
				Collections.singletonList(new SimpleGrantedAuthority(role))
			);
			SecurityContextHolder.getContext().setAuthentication(authentication);

		} catch (CustomJwtException e) {
			log.error("JWT Authentication Error: {}", e.getMessage());
			sendErrorResponse(response, e);
			return;
		}
		filterChain.doFilter(request, response);
	}
	private void sendErrorResponse(HttpServletResponse response, CustomJwtException e) throws IOException {
		log.error("JWT Authentication Error: {}", e.getMessage());

		BaseErrorResponse errorResponse = new BaseErrorResponse(INVALID_TOKEN, e.getMessage());

		response.setStatus(INVALID_TOKEN.getStatus());
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}