package net.mysite.SocialMedia.err;

public class PagePostMediaNotFoundException extends RuntimeException{
    public PagePostMediaNotFoundException(String msg){
        super(msg);
    }
}
