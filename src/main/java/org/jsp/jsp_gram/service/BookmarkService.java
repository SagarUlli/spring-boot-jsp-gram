package org.jsp.jsp_gram.service;

import java.util.List;

import org.jsp.jsp_gram.dto.ApiResponse;
import org.jsp.jsp_gram.dto.PostResponse;
import org.jsp.jsp_gram.exception.AuthException;
import org.jsp.jsp_gram.mapper.PostMapper;
import org.jsp.jsp_gram.model.Post;
import org.jsp.jsp_gram.model.User;
import org.jsp.jsp_gram.repository.PostRepository;
import org.jsp.jsp_gram.repository.UserRepository;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkService {

	private final UserRepository userRepository;
	private final PostRepository postRepository;

	/**
	 * Returns the latest logged-in user from the database.
	 */
	private User getLoggedInUser(HttpSession session) {

		User sessionUser = (User) session.getAttribute("user");

		if (sessionUser == null) {
			throw new AuthException("Not Logged In");
		}

		User user = userRepository.findById(sessionUser.getId()).orElseThrow(() -> new AuthException("User not found"));

		session.setAttribute("user", user);

		return user;
	}

	/**
	 * Bookmark a post.
	 */
	public ApiResponse<PostResponse> bookmarkPost(int postId, HttpSession session) {

		User user = getLoggedInUser(session);

		Post post = postRepository.findById(postId).orElseThrow(() -> new AuthException("Post not found"));

		boolean alreadyBookmarked = user.getBookmarkedPosts().stream()
				.anyMatch(bookmarkedPost -> bookmarkedPost.getId() == post.getId());

		if (!alreadyBookmarked) {
			user.getBookmarkedPosts().add(post);
			userRepository.save(user);
		}

		return new ApiResponse<>(true, alreadyBookmarked ? "Post already bookmarked" : "Post bookmarked successfully",
				PostMapper.toResponse(post, user));
	}

	/**
	 * Remove a post from bookmarks.
	 */
	public ApiResponse<PostResponse> unbookmarkPost(int postId, HttpSession session) {

		User user = getLoggedInUser(session);

		Post post = postRepository.findById(postId).orElseThrow(() -> new AuthException("Post not found"));

		boolean removed = user.getBookmarkedPosts().removeIf(bookmarkedPost -> bookmarkedPost.getId() == post.getId());

		if (removed) {
			userRepository.save(user);
		}

		return new ApiResponse<>(true, removed ? "Post removed from bookmarks" : "Post was not bookmarked",
				PostMapper.toResponse(post, user));
	}

	/**
	 * Get all posts bookmarked by the logged-in user.
	 */
	public ApiResponse<List<PostResponse>> getBookmarks(HttpSession session) {

		User user = getLoggedInUser(session);

		List<PostResponse> response = user.getBookmarkedPosts().stream().map(post -> PostMapper.toResponse(post, user))
				.toList();

		return new ApiResponse<>(true, "Bookmarks fetched successfully", response);
	}
}