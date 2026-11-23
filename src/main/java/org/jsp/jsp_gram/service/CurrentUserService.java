package org.jsp.jsp_gram.service;

import org.jsp.jsp_gram.exception.AuthException;
import org.jsp.jsp_gram.model.User;
import org.jsp.jsp_gram.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * Gives services access to the user making the current request. The user id
 * comes from the validated JWT (see JwtAuthenticationFilter); the full User is
 * loaded fresh from the database each time, as before.
 */
@Service
@RequiredArgsConstructor
public class CurrentUserService {

	private final UserRepository userRepository;

	/** Id of the authenticated user, without touching the database. */
	public int getCurrentUserId() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !(authentication.getPrincipal() instanceof Integer userId)) {
			throw new AuthException("Not Logged In");
		}

		return userId;
	}

	public User getLoggedInUser() {

		return userRepository.findById(getCurrentUserId()).orElseThrow(() -> new AuthException("User not found"));
	}
}
