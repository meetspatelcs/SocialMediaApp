package net.mysite.SocialMedia.err;

public class EmptyFriendsException extends RuntimeException{
    public EmptyFriendsException(String msg){
     super(msg);
    }
}
