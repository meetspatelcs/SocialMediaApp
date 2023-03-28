package net.mysite.SocialMedia.err;

public class LikeNotFoundException extends RuntimeException{
    public LikeNotFoundException(String msg){
        super(msg);
    }
}
