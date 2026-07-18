package org.jsp.jsp_gram.mapper;

import org.jsp.jsp_gram.dto.CommentResponse;
import org.jsp.jsp_gram.model.Comment;

public class CommentMapper {

	private CommentMapper() {
	}

	public static CommentResponse toResponse(Comment comment) {

		return new CommentResponse(comment.getId(), comment.getComment(), comment.getCommentedTime(),
				UserMapper.toResponse(comment.getUser()));
	}
}