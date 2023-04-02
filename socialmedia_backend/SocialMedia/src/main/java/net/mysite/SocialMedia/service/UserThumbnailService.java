package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.domain.UserThumbnail;
import net.mysite.SocialMedia.err.ThumbnailNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;

public interface UserThumbnailService {

    void save(User user) throws IllegalArgumentException;

    UserThumbnail updateThumbnail(User user, MultipartFile myFile) throws IOException, ThumbnailNotFoundException, NullPointerException;

    byte[] getImgById(User user) throws IOException, ThumbnailNotFoundException, NullPointerException;

    UserThumbnail removeThumbnail(User user) throws ThumbnailNotFoundException;
}
