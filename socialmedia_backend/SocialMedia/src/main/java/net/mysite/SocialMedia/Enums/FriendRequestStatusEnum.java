package net.mysite.SocialMedia.Enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FriendRequestStatusEnum {
    PENDING_REQUEST("Pending", 1),

    ACCEPTED_REQUEST("Friends", 2);

    private String status;
    private Integer number;

    FriendRequestStatusEnum(String status, Integer number){
        this.status = status;
        this.number = number;
    }

    public String getStatus(){
        return status;
    }

    public Integer getNumber() {
        return number;
    }
}
