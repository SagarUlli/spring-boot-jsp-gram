package org.jsp.jsp_gram.mapper;

import org.jsp.jsp_gram.dto.PostResponse;
import org.jsp.jsp_gram.model.Post;
import org.jsp.jsp_gram.model.User;

public class PostMapper {

	private PostMapper() {
	}

	public static PostResponse toResponse(Post post) {

		return new PostResponse(post.getId(), post.getCaption(), post.getImageUrl(), post.getPostedTime(),
				UserMapper.toResponse(post.getUser()), post.getLikedUsers().size(), post.getComments().size(), false,
				false);
	}

	public static PostResponse toResponse(Post post, User loggedInUser) {

		boolean liked = post.getLikedUsers().stream().anyMatch(user -> user.getId() == loggedInUser.getId());

		boolean ownPost = post.getUser().getId() == loggedInUser.getId();

		return new PostResponse(post.getId(), post.getCaption(), post.getImageUrl(), post.getPostedTime(),
				UserMapper.toResponse(post.getUser()), post.getLikedUsers().size(), post.getComments().size(), liked,
				ownPost);
	}
}