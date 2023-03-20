package net.mysite.SocialMedia.dto;

import net.mysite.SocialMedia.Enums.FriendRequestStatusEnum;
import net.mysite.SocialMedia.domain.Friend;

public class FriendDto {

    private Friend friend;
//    private Long requestedUser;
//    private Long myuserId;
//    private String status;
    private FriendRequestStatusEnum[] statusEnums = FriendRequestStatusEnum.values();

    public FriendDto(Friend friend){
        super();
        this.friend = friend;
    }

//    public Long getRequestedUser() {
//        return requestedUser;
//    }
//
//    public void setRequestedUser(Long requestedUser) {
//        this.requestedUser = requestedUser;
//    }
//
//    public Long getUser() {
//        return myuserId;
//    }
//
//    public void setUser(Long user) {
//        this.myuserId = user;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }

    public void setStatusEnums(FriendRequestStatusEnum[] statusEnums) {
        this.statusEnums = statusEnums;
    }

    public FriendRequestStatusEnum[] getStatusEnums() {
        return statusEnums;
    }
}
