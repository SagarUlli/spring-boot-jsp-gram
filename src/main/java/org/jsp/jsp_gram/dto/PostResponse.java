package org.jsp.jsp_gram.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

	private Integer id;

	private String caption;

	private String imageUrl;

	private LocalDateTime postedTime;

	private UserSummaryResponse user;

	private int likeCount;

	private int commentCount;

	private boolean liked;

	private boolean ownPost;

}