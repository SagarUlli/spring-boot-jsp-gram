package org.jsp.jsp_gram.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
	private long mobile;
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
