package com.example.hanaro.global.jwt;

import com.example.hanaro.global.jwt.exception.CustomJwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

	private final SecretKey secretKey;
	private final long accessTokenExpMs;

	public JwtUtil(
		@Value("${jwt.secret.key}") String secret,
		@Value("${jwt.token.expired-time-ms}") long accessTokenExpMs
	) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.accessTokenExpMs = accessTokenExpMs;
	}

	public String createAccessToken(String email, String role) {
		return Jwts.builder()
			.setHeader(Map.of("typ", "JWT"))
			.claim("email", email)
			.claim("role", role)
			.setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
			.setExpiration(new Date(System.currentTimeMillis() + accessTokenExpMs))
			.signWith(secretKey)
			.compact();
	}

	public Claims validateToken(String token) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (MalformedJwtException e) {
			throw new CustomJwtException("Malformed Token"); // 형식이 잘못된 토큰
		} catch (ExpiredJwtException e) {
			throw new CustomJwtException("Expired Token"); // 만료된 토큰
		} catch (JwtException e) {
			throw new CustomJwtException("Invalid Token"); // 그 외 유효하지 않은 토큰
		}
	}
}