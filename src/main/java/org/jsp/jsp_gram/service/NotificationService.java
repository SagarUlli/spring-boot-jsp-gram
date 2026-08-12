package org.jsp.jsp_gram.service;

import java.time.LocalDateTime;
import java.util.List;

import org.jsp.jsp_gram.dto.ApiResponse;
import org.jsp.jsp_gram.dto.NotificationResponse;
import org.jsp.jsp_gram.exception.AuthException;
import org.jsp.jsp_gram.mapper.NotificationMapper;
import org.jsp.jsp_gram.model.Notification;
import org.jsp.jsp_gram.model.Post;
import org.jsp.jsp_gram.model.User;
import org.jsp.jsp_gram.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final SessionService sessionService;

	/**
	 * Create a FOLLOW notification.
	 */
	public void createFollowNotification(User sender, User recipient) {

		if (sender.getId() == recipient.getId()) {
			return;
		}

		Notification notification = new Notification();

		notification.setMessage(sender.getUsername() + " started following you");

		notification.setType("FOLLOW");
		notification.setRead(false);
		notification.setCreatedTime(LocalDateTime.now());
		notification.setSender(sender);
		notification.setRecipient(recipient);

		notificationRepository.save(notification);
	}

	/**
	 * Create a LIKE notification.
	 */
	public void createLikeNotification(User sender, Post post) {

		User recipient = post.getUser();

		if (recipient == null || sender.getId() == recipient.getId()) {
			return;
		}

		Notification notification = new Notification();

		notification.setMessage(sender.getUsername() + " liked your post");

		notification.setType("LIKE");
		notification.setRead(false);
		notification.setCreatedTime(LocalDateTime.now());
		notification.setSender(sender);
		notification.setRecipient(recipient);
		notification.setPost(post);

		notificationRepository.save(notification);
	}

	/**
	 * Create a COMMENT notification.
	 */
	public void createCommentNotification(User sender, Post post) {

		User recipient = post.getUser();

		if (recipient == null || sender.getId() == recipient.getId()) {
			return;
		}

		Notification notification = new Notification();

		notification.setMessage(sender.getUsername() + " commented on your post");

		notification.setType("COMMENT");
		notification.setRead(false);
		notification.setCreatedTime(LocalDateTime.now());
		notification.setSender(sender);
		notification.setRecipient(recipient);
		notification.setPost(post);

		notificationRepository.save(notification);
	}

	/**
	 * Get all notifications for the logged-in user.
	 */
	public ApiResponse<List<NotificationResponse>> getNotifications(HttpSession session) {

		User user = sessionService.getLoggedInUser(session);

		List<NotificationResponse> notifications = notificationRepository.findByRecipientOrderByCreatedTimeDesc(user)
				.stream().map(NotificationMapper::toResponse).toList();

		return new ApiResponse<>(true, "Notifications fetched successfully", notifications);
	}

	/**
	 * Get unread notification count.
	 */
	public ApiResponse<Long> getUnreadCount(HttpSession session) {

		User user = sessionService.getLoggedInUser(session);

		long count = notificationRepository.countByRecipientAndReadFalse(user);

		return new ApiResponse<>(true, "Unread notification count fetched successfully", count);
	}

	/**
	 * Mark a single notification as read.
	 */
	public ApiResponse<Void> markAsRead(int id, HttpSession session) {

		User user = sessionService.getLoggedInUser(session);

		Notification notification = notificationRepository.findById(id)
				.orElseThrow(() -> new AuthException("Notification not found"));

		if (notification.getRecipient().getId() != user.getId()) {
			throw new AuthException("Unauthorized");
		}

		notification.setRead(true);

		notificationRepository.save(notification);

		return new ApiResponse<>(true, "Notification marked as read");
	}

	/**
	 * Mark all notifications as read.
	 */
	public ApiResponse<Void> markAllAsRead(HttpSession session) {

		User user = sessionService.getLoggedInUser(session);

		List<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedTimeDesc(user);

		notifications.forEach(notification -> notification.setRead(true));

		notificationRepository.saveAll(notifications);

		return new ApiResponse<>(true, "All notifications marked as read");
	}
}