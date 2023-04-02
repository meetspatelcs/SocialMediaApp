package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.domain.Posts;
import net.mysite.SocialMedia.domain.PostsPhoto;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.err.PostMediaNotFoundException;
import net.mysite.SocialMedia.err.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.Set;



public interface PostsPhotoService {

     PostsPhoto save(Posts newPost, User user, MultipartFile myFile) throws IOException;

     PostsPhoto getById(Long postId) throws PostMediaNotFoundException;

     ResponseEntity<byte[]> getVideoById(Long postId, String range) throws IOException, NullPointerException, PostMediaNotFoundException;

     byte[] getImgById(Long postId) throws IOException, PostMediaNotFoundException, NullPointerException;

     Set<PostsPhoto> findAllImg(User user);

     Set<PostsPhoto> findAllImgHandler(Long userId) throws UserNotFoundException;

     void mediaUpdateHelper(PostsPhoto postsPhoto, User user, MultipartFile file) throws IOException;

     void deleteById(Long delId);
}