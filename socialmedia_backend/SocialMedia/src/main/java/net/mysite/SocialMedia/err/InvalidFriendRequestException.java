package net.mysite.SocialMedia.err;

public class InvalidFriendRequestException extends RuntimeException{
    public InvalidFriendRequestException(String msg){
        super(msg);
    }
}
