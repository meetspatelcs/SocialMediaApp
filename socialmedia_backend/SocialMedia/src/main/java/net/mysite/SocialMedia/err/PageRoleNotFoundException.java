package net.mysite.SocialMedia.err;

public class PageRoleNotFoundException extends RuntimeException{
    public PageRoleNotFoundException(String msg){
        super(msg);
    }
}
