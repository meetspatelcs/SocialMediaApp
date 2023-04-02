package net.mysite.SocialMedia.company.sevice;

import net.mysite.SocialMedia.company.domain.PagePost;
import net.mysite.SocialMedia.company.domain.PagePostPhoto;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.err.PagePostMediaNotFoundException;
import net.mysite.SocialMedia.err.PagePostNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;

public interface PagePostPhotoService {

     PagePostPhoto save(PagePost newPagePost, MultipartFile myFile, User user) throws IOException, PagePostNotFoundException;

     PagePostPhoto getById(Long pageId, Long postId) throws PagePostMediaNotFoundException;

     void updateSave(PagePostPhoto pagePostPhoto, MultipartFile myFile, User user) throws IOException;

     ResponseEntity<byte[]> getVideoById(Long pageId, Long pagePostId, String range) throws IOException, PagePostMediaNotFoundException,NullPointerException;

     byte[] getImgById(Long pageId, Long postId) throws IOException, PagePostMediaNotFoundException, NullPointerException;
}
