package net.mysite.SocialMedia.err;

public class ThumbnailNotFoundException extends RuntimeException{
    public ThumbnailNotFoundException(String msg) {
        super(msg);
    }
}
