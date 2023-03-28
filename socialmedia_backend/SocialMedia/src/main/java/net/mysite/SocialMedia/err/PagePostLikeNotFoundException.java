package net.mysite.SocialMedia.err;

public class PagePostLikeNotFoundException extends RuntimeException{
    public PagePostLikeNotFoundException(String msg){
        super(msg);
    }
}
