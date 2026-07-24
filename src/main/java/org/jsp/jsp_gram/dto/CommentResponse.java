package org.jsp.jsp_gram.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {

	private Integer id;

	private String comment;

	private LocalDateTime commentedTime;

	private UserResponse user;

}