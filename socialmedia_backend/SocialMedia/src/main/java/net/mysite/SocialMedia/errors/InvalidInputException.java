package net.mysite.SocialMedia.errors;

public class InvalidInputException extends RuntimeException{
    public InvalidInputException(String message){
        super(message);
    }
}
