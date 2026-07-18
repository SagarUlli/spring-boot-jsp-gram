package org.jsp.jsp_gram.mapper;

import org.jsp.jsp_gram.dto.PostResponse;
import org.jsp.jsp_gram.model.Post;

public class PostMapper {

	private PostMapper() {
	}

	public static PostResponse toResponse(Post post) {

		return new PostResponse(post.getId(), post.getCaption(), post.getImageUrl(), post.getPostedTime(),
				UserMapper.toResponse(post.getUser()), post.getLikedUsers().size(), post.getComments().size(), false);
	}
}