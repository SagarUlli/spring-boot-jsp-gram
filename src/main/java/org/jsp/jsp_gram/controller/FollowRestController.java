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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowRestController {

	private final FollowService followService;

	@GetMapping("/suggestions")
	public ApiResponse<List<UserResponse>> getSuggestions() {

		return followService.getSuggestions();
	}

	@PostMapping("/{id}/follow")
	public ApiResponse<Void> followUser(@PathVariable int id) {

		return followService.followUser(id);
	}

	@DeleteMapping("/{id}/follow")
	public ApiResponse<Void> unfollowUser(@PathVariable int id) {

		return followService.unfollowUser(id);
	}

	@GetMapping("/followers")
	public ApiResponse<List<UserResponse>> getFollowers() {

		return followService.getFollowers();
	}

	@GetMapping("/following")
	public ApiResponse<List<UserResponse>> getFollowing() {

		return followService.getFollowing();
	}
}