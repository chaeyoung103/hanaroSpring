package com.example.hanaro.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
	private final Key secretKey;
	private final long expiredTimeMs;

	public JwtUtil(@Value("${jwt.secret.key}") String secretKey,
		@Value("${jwt.token.expired-time-ms}") long expiredTimeMs) {
		this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
		this.expiredTimeMs = expiredTimeMs;
	}

	// JWT 토큰 생성
	public String createToken(String email, String role) {
		Claims claims = Jwts.claims();
		claims.put("email", email);
		claims.put("role", role);

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs))
			.signWith(secretKey)
			.compact();
	}
}