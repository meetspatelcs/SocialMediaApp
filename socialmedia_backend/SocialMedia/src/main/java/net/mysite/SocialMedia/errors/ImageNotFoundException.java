package net.mysite.SocialMedia.errors;

public class ImageNotFoundException extends RuntimeException{

    public ImageNotFoundException(String message){
        super(message);
    }
}
