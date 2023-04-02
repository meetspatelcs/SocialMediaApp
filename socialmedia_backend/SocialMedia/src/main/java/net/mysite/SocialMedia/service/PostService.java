package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.domain.Likes;
import net.mysite.SocialMedia.domain.Posts;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.err.LikeNotFoundException;
import net.mysite.SocialMedia.err.MissingFieldException;
import net.mysite.SocialMedia.err.PostMediaNotFoundException;
import net.mysite.SocialMedia.err.UserNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Set;

public interface PostService {

     Posts save(String description, User user) throws MissingFieldException;

     Posts getById(Long postId) throws NullPointerException;

     List<Posts> findByUser(User user);

     void deleteById(Long delId);

     Posts updatePost(Long postId, String description, MultipartFile myFile, User user) throws NullPointerException, PostMediaNotFoundException;

     List<Posts> findByUserHandler(Long userId) throws UserNotFoundException;

     Set<Object> getTodayPostsByFriendAndPage(User user);

     Likes increaseLike(Long postId, User user) throws NullPointerException;

     Likes checkIfPostLiked(Long postId, User user) throws LikeNotFoundException;

     Boolean removePostLike(Long postId, User user);
}