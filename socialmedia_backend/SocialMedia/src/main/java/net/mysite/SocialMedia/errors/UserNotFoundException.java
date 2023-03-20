package net.mysite.SocialMedia.errors;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message){
        super(message);
    }
}
