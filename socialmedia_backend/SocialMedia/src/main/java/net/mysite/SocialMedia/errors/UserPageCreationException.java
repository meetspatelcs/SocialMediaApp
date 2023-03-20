package net.mysite.SocialMedia.errors;

public class UserPageCreationException extends RuntimeException{
    public UserPageCreationException(String message){
        super(message);
    }
}
