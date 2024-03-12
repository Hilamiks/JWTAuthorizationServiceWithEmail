package com.hailmik.lorbyproject.service;

import com.hailmik.lorbyproject.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
	private final String SECRET_KEY = "0d7474d4b363cdeeaca91dc2197770797cd7b17270a29105b8464011bbcf19db";

	public String generateToken(User user) {
		return Jwts.builder()
			.subject(user.getUsername())
			.issuedAt(new Date(System.currentTimeMillis()))
			.signWith(generateKey())
			.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
			.compact();
	}

	private SecretKey generateKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parser()
			.verifyWith(generateKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	public<T> T extractClaim(String token, Function<Claims, T> resolver) {
		return resolver.apply(extractAllClaims(token));
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public boolean tokenIsExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public boolean isValid(String token, UserDetails user) {
		String username = extractUsername(token);
		return user.getUsername().equals(username) && !tokenIsExpired(token);
	}
}
