package org.jsp.jsp_gram.mapper;

import org.jsp.jsp_gram.dto.PostResponse;
import org.jsp.jsp_gram.model.Post;
import org.jsp.jsp_gram.model.User;

public final class PostMapper {

	private PostMapper() {
	}

	public static PostResponse toResponse(Post post) {

		return new PostResponse(

				post.getId(),

				post.getCaption(),

				post.getImageUrl(),

				post.getPostedTime(),

				UserMapper.toSummaryResponse(post.getUser()),

				post.getLikedUsers().size(),

				post.getComments().size(),

				false,

				false,

				false

		);
	}

	public static PostResponse toResponse(Post post, User loggedInUser) {

		if (loggedInUser == null) {
			return toResponse(post);
		}

		boolean liked = post.getLikedUsers().stream().anyMatch(user -> user.getId() == loggedInUser.getId());

		boolean ownPost = post.getUser().getId() == loggedInUser.getId();

		boolean bookmarked = loggedInUser.getBookmarkedPosts().stream()
				.anyMatch(bookmarkedPost -> bookmarkedPost.getId() == post.getId());

		return new PostResponse(

				post.getId(),

				post.getCaption(),

				post.getImageUrl(),

				post.getPostedTime(),

				UserMapper.toSummaryResponse(post.getUser()),

				post.getLikedUsers().size(),

				post.getComments().size(),

				liked,

				ownPost,

				bookmarked

		);
	}
}	