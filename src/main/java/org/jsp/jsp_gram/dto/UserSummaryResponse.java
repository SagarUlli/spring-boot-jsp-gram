package org.jsp.jsp_gram.dto;

import lombok.Data;

@Data
public class UserSummaryResponse {

    private int id;
    private String firstname;
    private String lastname;
    private String username;
    private String imageUrl;

    public UserSummaryResponse(int id, String firstname, String lastname,
                               String username, String imageUrl) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.imageUrl = imageUrl;
    }
}