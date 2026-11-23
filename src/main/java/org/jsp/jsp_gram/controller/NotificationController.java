package org.jsp.jsp_gram.controller;

import java.util.List;

import org.jsp.jsp_gram.dto.ApiResponse;
import org.jsp.jsp_gram.dto.NotificationResponse;
import org.jsp.jsp_gram.service.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping
	public ApiResponse<List<NotificationResponse>> getNotifications() {

		return notificationService.getNotifications();
	}

	@GetMapping("/unread-count")
	public ApiResponse<Long> getUnreadCount() {

		return notificationService.getUnreadCount();
	}

	@PostMapping("/{id}/read")
	public ApiResponse<Void> markAsRead(@PathVariable int id) {

		return notificationService.markAsRead(id);
	}

	@PostMapping("/read-all")
	public ApiResponse<Void> markAllAsRead() {

		return notificationService.markAllAsRead();
	}
}