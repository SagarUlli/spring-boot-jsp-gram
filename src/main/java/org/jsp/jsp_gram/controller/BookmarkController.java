package org.jsp.jsp_gram.controller;

import java.util.List;

import org.jsp.jsp_gram.dto.ApiResponse;
import org.jsp.jsp_gram.dto.PostResponse;
import org.jsp.jsp_gram.service.BookmarkService;
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
public class BookmarkController {

	private final BookmarkService bookmarkService;

	/**
	 * Bookmark a post.
	 */
	@PostMapping("/bookmarks/{postId}")
	public ApiResponse<PostResponse> bookmarkPost(@PathVariable int postId, HttpSession session) {

		return bookmarkService.bookmarkPost(postId, session);
	}

	/**
	 * Remove a post from bookmarks.
	 */
	@DeleteMapping("/bookmarks/{postId}")
	public ApiResponse<PostResponse> unbookmarkPost(@PathVariable int postId, HttpSession session) {

		return bookmarkService.unbookmarkPost(postId, session);
	}

	/**
	 * Get all bookmarked posts.
	 */
	@GetMapping("/bookmarks")
	public ApiResponse<List<PostResponse>> getBookmarks(HttpSession session) {

		return bookmarkService.getBookmarks(session);
	}
}