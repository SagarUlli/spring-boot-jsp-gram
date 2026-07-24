package org.jsp.jsp_gram.mapper;

import java.util.List;

import org.jsp.jsp_gram.dto.PostResponse;
import org.jsp.jsp_gram.dto.UserResponse;
import org.jsp.jsp_gram.dto.UserSummaryResponse;
import org.jsp.jsp_gram.model.User;

public class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {

        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setFirstname(user.getFirstname());
        response.setLastname(user.getLastname());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setMobile(user.getMobile());
        response.setGender(user.getGender());

        response.setBio(user.getBio());
        response.setImageUrl(user.getImageUrl());

        response.setPrime(user.isPrime());

        response.setFollowersCount(user.getFollowers().size());
        response.setFollowingCount(user.getFollowing().size());

        List<PostResponse> posts = user.getPosts()
                .stream()
                .map(PostMapper::toResponse)
                .toList();

        response.setPosts(posts);
        response.setPostCount(posts.size());

        return response;
    }


    public static UserSummaryResponse toSummaryResponse(User user) {

        if (user == null) {
            return null;
        }

        return new UserSummaryResponse(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getUsername(),
                user.getImageUrl()
        );
    }
}