package org.jsp.jsp_gram.repository;

import java.util.List;

import org.jsp.jsp_gram.model.Notification;
import org.jsp.jsp_gram.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

	List<Notification> findByRecipientOrderByCreatedTimeDesc(User recipient);

	long countByRecipientAndReadFalse(User recipient);
}