package net.mysite.SocialMedia.err;

public class NotFriendException extends RuntimeException{
    public NotFriendException(String msg){
        super(msg);
    }
}
