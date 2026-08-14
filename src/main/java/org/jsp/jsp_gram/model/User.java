package org.jsp.jsp_gram.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Data
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String firstname;
	private String lastname;
	private String username;
	private String email;
	private String mobile;
	private String password;
	@Transient
	private String confirmpassword;
	private String gender;
	private int otp;
	private boolean verified;
	private String bio;
	private String imageUrl;
	private boolean prime;

	@ManyToMany(fetch = FetchType.EAGER)
	private List<User> following = new ArrayList<>();

	@ManyToMany(fetch = FetchType.EAGER)
	private List<User> followers = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Post> posts = new ArrayList<>();

	@ManyToMany
	@JoinTable(name = "user_bookmarks", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "post_id"))
	private Set<Post> bookmarkedPosts = new HashSet<>();

	/**
	 * Check if this user is followed by another user
	 */
	public boolean isFollowedBy(User other) {
		if (other == null)
			return false;

		for (User u : other.getFollowing()) {
			if (u.getId() == this.id) {
				return true;
			}
		}
		return false;
	}

}
