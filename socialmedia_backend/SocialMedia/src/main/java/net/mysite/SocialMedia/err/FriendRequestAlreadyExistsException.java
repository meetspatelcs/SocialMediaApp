package net.mysite.SocialMedia.err;

public class FriendRequestAlreadyExistsException extends RuntimeException{
    public FriendRequestAlreadyExistsException(String msg){
        super(msg);
    }

}
