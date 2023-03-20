package net.mysite.SocialMedia.errors;

public class FriendNotFoundException extends RuntimeException{
    public FriendNotFoundException(String message){
        super(message);
    }
}
