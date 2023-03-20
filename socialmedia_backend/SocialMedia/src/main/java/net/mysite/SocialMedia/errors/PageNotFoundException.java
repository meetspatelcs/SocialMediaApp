package net.mysite.SocialMedia.errors;

public class PageNotFoundException extends RuntimeException{
    public PageNotFoundException(String message){
        super(message);
    }
}
