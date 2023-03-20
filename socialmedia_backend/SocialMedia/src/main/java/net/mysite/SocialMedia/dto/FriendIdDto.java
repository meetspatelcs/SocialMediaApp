package net.mysite.SocialMedia.dto;

import org.springframework.beans.factory.annotation.Value;


public class FriendIdDto {

    private Long id;
    private String status;

    public FriendIdDto(Long id, String status){
        this.id = id;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

}
