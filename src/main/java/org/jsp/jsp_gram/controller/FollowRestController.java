package org.jsp.jsp_gram.controller;

import java.util.List;

import org.jsp.jsp_gram.dto.ApiResponse;
import org.jsp.jsp_gram.dto.UserResponse;
import org.jsp.jsp_gram.service.FollowService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowRestController {

	private final FollowService followService;

	@GetMapping("/suggestions")
	public ApiResponse<List<UserResponse>> getSuggestions(HttpSession session) {

		return followService.getSuggestions(session);
	}

	@PostMapping("/{id}/follow")
	public ApiResponse<Void> followUser(@PathVariable int id, HttpSession session) {

		return followService.followUser(id, session);
	}

	@DeleteMapping("/{id}/follow")
	public ApiResponse<Void> unfollowUser(@PathVariable int id, HttpSession session) {

		return followService.unfollowUser(id, session);
	}

	@GetMapping("/followers")
	public ApiResponse<List<UserResponse>> getFollowers(HttpSession session) {

		return followService.getFollowers(session);
	}

	@GetMapping("/following")
	public ApiResponse<List<UserResponse>> getFollowing(HttpSession session) {

		return followService.getFollowing(session);
	}
}