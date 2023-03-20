package net.mysite.SocialMedia.errors;

public class DatabaseException extends RuntimeException{
    public DatabaseException(String message){
        super(message);
    }
}
