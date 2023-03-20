package net.mysite.SocialMedia.errors;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message){
        super(message);
    }
}
