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

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping
	public ApiResponse<List<NotificationResponse>> getNotifications(HttpSession session) {

		return notificationService.getNotifications(session);
	}

	@GetMapping("/unread-count")
	public ApiResponse<Long> getUnreadCount(HttpSession session) {

		return notificationService.getUnreadCount(session);
	}

	@PostMapping("/{id}/read")
	public ApiResponse<Void> markAsRead(@PathVariable int id, HttpSession session) {

		return notificationService.markAsRead(id, session);
	}

	@PostMapping("/read-all")
	public ApiResponse<Void> markAllAsRead(HttpSession session) {

		return notificationService.markAllAsRead(session);
	}
}