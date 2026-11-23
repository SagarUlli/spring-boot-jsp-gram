package org.jsp.jsp_gram.repository;

import org.jsp.jsp_gram.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

}