package org.jsp.jsp_gram.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Data
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String imageUrl;
	private String caption;

	@UpdateTimestamp
	private LocalDateTime postedTime;

	@ManyToOne
	private User user;

	@Transient
	private MultipartFile image;

	@ManyToMany(fetch = FetchType.EAGER)
	private Set<User> likedUsers = new HashSet<>();

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments = new ArrayList<>();

	/**
	 * Check if a user has liked this post
	 */
	public boolean hasLiked(int userId) {
		return likedUsers.stream().anyMatch(u -> u.getId() == userId);
	}
}
