package org.jsp.jsp_gram.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

	private int id;

	private String message;

	private String type;

	private boolean read;

	private LocalDateTime createdTime;

	private int senderId;

	private String senderUsername;

	private String senderImageUrl;

	private Integer postId;
}