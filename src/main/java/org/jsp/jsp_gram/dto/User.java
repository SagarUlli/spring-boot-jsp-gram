package org.jsp.jsp_gram.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
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

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_following", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "following_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"user_id", "following_id" }))
	private List<User> following;

	@JsonIgnore
	@ManyToMany(mappedBy = "following", fetch = FetchType.EAGER)
	private List<User> followers;

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
