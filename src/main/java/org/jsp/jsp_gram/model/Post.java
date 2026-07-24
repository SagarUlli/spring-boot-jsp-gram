package org.jsp.jsp_gram.model;

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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
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

    // Post owner

    @ManyToOne
    private User user;

    // Used only for image upload

    @Transient
    private MultipartFile image;

    // Users who liked this post

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "post_likes",

        joinColumns = @JoinColumn(name = "post_id"),

        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likedUsers = new HashSet<>();

    // Comments on this post

    @OneToMany(
        mappedBy = "post",
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Comment> comments = new ArrayList<>();
    /*
     * Check if current user liked this post
     */

    public boolean hasLiked(int userId) {

        return likedUsers
                .stream()
                .anyMatch(user -> user.getId() == userId);

    }
    /*
     * Add like
     */

    public void addLike(User user) {

        likedUsers.add(user);

    }
    /*
     * Remove like
     */

    public void removeLike(User user) {

        likedUsers.remove(user);

    }
    /*
     * Total likes count
     */

    public int getLikesCount() {

        return likedUsers.size();

    }
    /*
     * Total comments count
     */

    public int getCommentsCount() {

        return comments.size();

    }
}