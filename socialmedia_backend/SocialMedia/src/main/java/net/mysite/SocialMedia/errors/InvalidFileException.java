package net.mysite.SocialMedia.errors;

public class InvalidFileException extends RuntimeException{
    public InvalidFileException(String message){
        super(message);
    }
}
