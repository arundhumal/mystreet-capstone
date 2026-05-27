package com.mystreet.security;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.mystreet.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtTokenProvider {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expiration}")
	private long jwtExpiration;

	private SecretKey signingKey;

	@PostConstruct
	private void init() {
		signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(User user) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpiration);

		return Jwts.builder().subject(user.getEmail()).claim("userId", user.getId()).claim("isAdmin", user.isAdmin())
				.issuedAt(now).expiration(expiryDate).signWith(signingKey, Jwts.SIG.HS512).compact();
	}

	private Claims getClaims(String token) {
		return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
	}

	/**
	 * Parses the token once and returns a fully populated authentication token.
	 * Throws JwtException if the token is invalid.
	 */
	public UsernamePasswordAuthenticationToken getAuthentication(String token) {
		Claims claims = getClaims(token);
		String email = claims.getSubject();
		boolean isAdmin = Boolean.TRUE.equals(claims.get("isAdmin", Boolean.class));

		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		if (isAdmin) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}

		return new UsernamePasswordAuthenticationToken(email, null, authorities);
	}
}