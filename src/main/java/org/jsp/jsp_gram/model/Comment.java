package org.jsp.jsp_gram.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String comment;

	@CreationTimestamp
	private LocalDateTime commentedTime;

	// User who wrote the comment

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	// Post on which comment is added

	@ManyToOne(fetch = FetchType.LAZY)
	private Post post;

}