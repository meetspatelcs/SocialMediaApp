package net.mysite.SocialMedia.err;

public class PageNotFoundException extends RuntimeException{
    public PageNotFoundException(String msg){
        super(msg);
    }
}
