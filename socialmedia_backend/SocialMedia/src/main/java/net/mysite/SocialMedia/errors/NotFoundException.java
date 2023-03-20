package net.mysite.SocialMedia.errors;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message){
        super(message);
    }
}
