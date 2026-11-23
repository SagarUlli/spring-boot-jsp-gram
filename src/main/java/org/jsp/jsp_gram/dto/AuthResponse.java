package org.jsp.jsp_gram.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Returned by login: the JWT plus the logged-in user's profile. */
@Getter
@AllArgsConstructor
public class AuthResponse {

	private String token;
	private String tokenType;
	private long expiresInSeconds;
	private UserResponse user;
}
