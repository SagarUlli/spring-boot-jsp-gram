package org.jsp.jsp_gram.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {

	@NotBlank(message = "Comment cannot be empty")
	private String comment;
}