package org.jsp.jsp_gram.service;

import org.jsp.jsp_gram.exception.AuthException;
import org.jsp.jsp_gram.model.User;
import org.jsp.jsp_gram.repository.UserRepository;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionService {

	private final UserRepository userRepository;

	public User getLoggedInUser(HttpSession session) {

		User sessionUser = (User) session.getAttribute("user");

		if (sessionUser == null) {
			throw new AuthException("Not Logged In");
		}

		User user = userRepository.findById(sessionUser.getId()).orElseThrow(() -> new AuthException("User not found"));

		session.setAttribute("user", user);

		return user;
	}
}