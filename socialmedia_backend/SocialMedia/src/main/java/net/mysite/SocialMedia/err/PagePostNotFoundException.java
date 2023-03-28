package net.mysite.SocialMedia.err;

public class PagePostNotFoundException extends RuntimeException{
    public PagePostNotFoundException(String msg){
        super(msg);
    }
}
