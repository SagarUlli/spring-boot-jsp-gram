package org.jsp.jsp_gram.repository;

import java.util.List;

import org.jsp.jsp_gram.model.Post;
import org.jsp.jsp_gram.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
	List<Post> findByUser(User user);

	List<Post> findByUserIn(List<User> users);

	List<Post> findByUserInOrderByPostedTimeDesc(List<User> users);
}
