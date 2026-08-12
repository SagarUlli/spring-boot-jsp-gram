package org.jsp.jsp_gram.repository;

import java.util.List;

import org.jsp.jsp_gram.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	boolean existsByEmail(String email);

	boolean existsByMobile(String mobile);

	boolean existsByUsername(String username);

	User findByUsername(String username);

	List<User> findByVerifiedTrue();

	List<User> findByUsernameContainingIgnoreCaseAndVerifiedTrue(String username);
}
