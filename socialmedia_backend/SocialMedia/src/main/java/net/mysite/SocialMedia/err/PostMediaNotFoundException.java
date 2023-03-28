package net.mysite.SocialMedia.err;

public class PostMediaNotFoundException extends RuntimeException{
    public PostMediaNotFoundException(String msg){
        super(msg);
    }
}
