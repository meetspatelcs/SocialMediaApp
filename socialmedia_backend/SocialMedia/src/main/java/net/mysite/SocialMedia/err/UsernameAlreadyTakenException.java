package net.mysite.SocialMedia.err;

public class UsernameAlreadyTakenException extends RuntimeException{
     public UsernameAlreadyTakenException(String msg){
         super(msg);
     }
}
