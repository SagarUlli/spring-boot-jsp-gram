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

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentRestController {

	private final CommentService commentService;

	@PostMapping("/{id}/comments")
	public ApiResponse<CommentResponse> addComment(@PathVariable int id, @RequestBody @Valid CommentRequest request,
			HttpSession session) {

		return commentService.addComment(id, request, session);
	}

	@GetMapping("/{id}/comments")
	public ApiResponse<List<CommentResponse>> getComments(@PathVariable int id, HttpSession session) {

		return commentService.getComments(id, session);
	}

	@DeleteMapping("/{id}")
	public ApiResponse<Void> deleteComment(@PathVariable int id, HttpSession session) {

		return commentService.deleteComment(id, session);
	}
}