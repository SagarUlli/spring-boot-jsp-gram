package org.jsp.jsp_gram.security;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

import javax.crypto.SecretKey;

import org.jsp.jsp_gram.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Creates and validates signed JWT access tokens. The token subject is the
 * user's id; nothing else is needed to authenticate a request.
 */
@Service
public class JwtService {

	private static final int MIN_SECRET_BYTES = 32; // HS256 needs a 256-bit key

	private final SecretKey key;
	private final Duration expiration;

	public JwtService(@Value("${jwt.secret}") String secret,
			@Value("${jwt.expiration-minutes:1440}") long expirationMinutes) {

		byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);

		if (bytes.length < MIN_SECRET_BYTES) {
			throw new IllegalStateException("jwt.secret (JWT_SECRET) must be at least 32 characters long");
		}

		this.key = Keys.hmacShaKeyFor(bytes);
		this.expiration = Duration.ofMinutes(expirationMinutes);
	}

	public String generateToken(User user) {

		Date now = new Date();

		return Jwts.builder().subject(String.valueOf(user.getId())).claim("username", user.getUsername())
				.issuedAt(now).expiration(new Date(now.getTime() + expiration.toMillis())).signWith(key).compact();
	}

	/**
	 * Validates the signature and expiry and returns the user id.
	 *
	 * @throws JwtException if the token is malformed, tampered with or expired
	 */
	public int parseUserId(String token) {

		Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

		try {
			return Integer.parseInt(claims.getSubject());
		} catch (NumberFormatException e) {
			throw new JwtException("Invalid token subject");
		}
	}

	public long getExpirationSeconds() {
		return expiration.toSeconds();
	}
}
