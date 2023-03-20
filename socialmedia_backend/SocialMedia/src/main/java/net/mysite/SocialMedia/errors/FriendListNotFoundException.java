package net.mysite.SocialMedia.errors;

public class FriendListNotFoundException extends RuntimeException{

    public FriendListNotFoundException(String message){
        super(message);
    }
}
