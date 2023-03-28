package net.mysite.SocialMedia.err;

public class InvalidFriendStatusException extends RuntimeException{
    public InvalidFriendStatusException(String msg){
        super(msg);
    }
}
