package org.jsp.jsp_gram.controller;

import java.util.List;

import org.jsp.jsp_gram.dto.ApiResponse;
import org.jsp.jsp_gram.dto.CommentRequest;
import org.jsp.jsp_gram.dto.CommentResponse;
import org.jsp.jsp_gram.service.CommentService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentRestController {

	private final CommentService commentService;

	// Add comment

	@PostMapping("/{postId}")
	public ApiResponse<CommentResponse> addComment(

			@PathVariable int postId,

			@RequestBody @Valid CommentRequest request

	) {

		return commentService.addComment(postId, request);

	}

	// Get comments of a post

	@GetMapping("/{postId}")
	public ApiResponse<List<CommentResponse>> getComments(

			@PathVariable int postId

	) {

		return commentService.getComments(postId);

	}

	// Delete comment

	@DeleteMapping("/{commentId}")
	public ApiResponse<Void> deleteComment(

			@PathVariable int commentId

	) {

		return commentService.deleteComment(commentId);

	}

}