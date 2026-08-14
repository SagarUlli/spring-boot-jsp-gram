package org.jsp.jsp_gram.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String message;

	private String type;

	@Column(name = "is_read", nullable = false)
	private boolean read;

	private LocalDateTime createdTime;

	@ManyToOne
	private User recipient;

	@ManyToOne
	private User sender;

	@ManyToOne
	private Post post;
}