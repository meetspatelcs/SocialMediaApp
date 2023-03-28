package net.mysite.SocialMedia.err;

public class MissingFieldException extends RuntimeException{
    public MissingFieldException(String msg){
        super(msg);
    }
}
