package net.mysite.SocialMedia.errors;

public class FriendRequestException extends RuntimeException{

    public FriendRequestException(String message){
        super(message);
    }
}
