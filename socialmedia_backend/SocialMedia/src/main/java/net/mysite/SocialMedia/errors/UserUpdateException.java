package net.mysite.SocialMedia.errors;

public class UserUpdateException extends RuntimeException{
    public UserUpdateException(String message){
        super(message);
    }
}
