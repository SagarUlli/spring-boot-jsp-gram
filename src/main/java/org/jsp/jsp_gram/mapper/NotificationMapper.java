package org.jsp.jsp_gram.mapper;

import org.jsp.jsp_gram.dto.NotificationResponse;
import org.jsp.jsp_gram.model.Notification;

public final class NotificationMapper {

	private NotificationMapper() {
	}

	public static NotificationResponse toResponse(Notification notification) {

		return NotificationResponse.builder().id(notification.getId()).message(notification.getMessage())
				.type(notification.getType()).read(notification.isRead()).createdTime(notification.getCreatedTime())
				.senderId(notification.getSender().getId()).senderUsername(notification.getSender().getUsername())
				.senderImageUrl(notification.getSender().getImageUrl())
				.postId(notification.getPost() != null ? notification.getPost().getId() : null).build();
	}
}