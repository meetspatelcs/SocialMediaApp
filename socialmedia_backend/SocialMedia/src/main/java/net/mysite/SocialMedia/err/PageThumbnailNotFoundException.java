package net.mysite.SocialMedia.err;

public class PageThumbnailNotFoundException extends RuntimeException{
    public PageThumbnailNotFoundException(String msg){
        super(msg);
    }
}
