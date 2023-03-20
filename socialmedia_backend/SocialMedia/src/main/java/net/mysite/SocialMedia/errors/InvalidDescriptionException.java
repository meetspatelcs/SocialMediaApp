package net.mysite.SocialMedia.errors;

public class InvalidDescriptionException extends RuntimeException{
    public InvalidDescriptionException(String message){
        super(message);
    }
}
