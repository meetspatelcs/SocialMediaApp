package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.Enums.FriendRequestStatusEnum;
import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.company.domain.PagePost;
import net.mysite.SocialMedia.company.repository.PagePostRepository;
import net.mysite.SocialMedia.domain.Likes;
import net.mysite.SocialMedia.domain.Posts;
import net.mysite.SocialMedia.domain.PostsPhoto;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.err.LikeNotFoundException;
import net.mysite.SocialMedia.err.MissingFieldException;
import net.mysite.SocialMedia.err.PostMediaNotFoundException;
import net.mysite.SocialMedia.err.UserNotFoundException;
import net.mysite.SocialMedia.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostsPhotoRepository postsPhotoRepository;
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserPageRepository userPageRepository;
    @Autowired
    private PagePostRepository pagePostRepository;
    @Autowired
    private LikesRepository likesRepository;
    @Autowired
    private PostsPhotoService postsPhotoService;

    public Posts save(String description, User user) {
        if(description.isEmpty()){ throw new MissingFieldException("Description cannot be null or empty."); }

        Posts post = new Posts();
        post.setUser(user);
        post.setCreationDate(LocalDateTime.now());
        post.setDescription(description);
        return postRepository.save(post);
    }

    public Posts getById(Long postId) {
        return postRepository.findById(postId).
                orElseThrow(() -> new NullPointerException("Post was not found."));
    }

    public List<Posts> findByUser(User user){
        List<Posts> userPostSet = postRepository.findByUser(user);
        if(userPostSet.isEmpty()) {return Collections.emptyList(); }
        return userPostSet;
    }

    private Set<User> convertFriendToUser(User user){
        Set<User> sentToUsers = friendRepository.findAllSent(user.getId(), FriendRequestStatusEnum.ACCEPTED_REQUEST.getStatus());

        Set<User> receivedFromUsers = friendRepository.findAllReceived(user.getId(), FriendRequestStatusEnum.ACCEPTED_REQUEST.getStatus());

        Set<User> friendToUserSet = new HashSet<>();
        friendToUserSet.addAll(sentToUsers);
        friendToUserSet.addAll(receivedFromUsers);
        friendToUserSet.add(user);

        return friendToUserSet;
    }

    public void deleteById(Long delId) { postRepository.deleteById(delId); }

    public Posts updatePost(Long postId, String description, MultipartFile myFile, User user) {
        Posts myPost = getById(postId);

        if(description != null){
            updatePostDescription(myPost, description);
            postRepository.save(myPost);
        }
        if(myFile != null){
            PostsPhoto updateMedia = postsPhotoRepository.findById(postId)
                    .orElseThrow(() -> new PostMediaNotFoundException("PostPhoto ref for post was not found."));
            updatePostMedia(updateMedia, myFile, user);
            postsPhotoRepository.save(updateMedia);
        }
        return myPost;
    }

    private void updatePostDescription(Posts post, String description){ post.setDescription(description); }

    private void updatePostMedia(PostsPhoto updateMedia, MultipartFile myFile, User user) {
        try{
            postsPhotoService.mediaUpdateHelper(updateMedia, user, myFile);
        }
        catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public List<Posts> findByUserHandler(Long userId) {
        if(!userRepository.existsById(userId)){ throw new UserNotFoundException("User does not exists."); }

        List<Posts> userPostList = postRepository.getByuserId(userId);
        if(userPostList.isEmpty()){ return Collections.emptyList(); }
        return userPostList;
    }

    public Set<Object> getTodayPostsByFriendAndPage(User user) {
        Set<Object> dashboardPosts = generatePostsOfFriendsAndPages(user);
        return dashboardPosts;
    }

    private Set<Object> generatePostsOfFriendsAndPages(User user){
        Set<User> myFriendSet = convertFriendToUser(user);
        Set<Object> resPosts = new HashSet<>();
        if(!myFriendSet.isEmpty()){
//            loggedUserPosts(user, resPosts);
//            return resPosts;
            generateUsersPosts(myFriendSet, resPosts);
        }
//        generateUsersPosts(myFriendSet, resPosts);
        loggedUserPosts(user, resPosts);
        Set<Page> pageSet = userPageRepository.findPageWithUser(user, "ROLE_USER");
        if(!pageSet.isEmpty()){ generatePagePosts(pageSet, resPosts); }
        return resPosts;
    }

    private void loggedUserPosts(User user, Set<Object> objSet){
        LocalDateTime endPoint = LocalDateTime.now().minusHours(24);
        List<Posts> currUserPost = postRepository.findPostWithTimeByUser(user, endPoint);
        if(currUserPost.isEmpty())
            return;

        objSet.addAll(currUserPost);
    }

    private void generateUsersPosts(Set<User> userSet, Set<Object> objSet){
        LocalDateTime endPoint = LocalDateTime.now().minusHours(24);
        for(User currUser : userSet){
            List<Posts> currUserPost = postRepository.findPostWithTimeByUser(currUser, endPoint);
            if(currUserPost == null || currUserPost.isEmpty())
                continue;

            objSet.addAll(currUserPost);
        }
    }

    private void generatePagePosts(Set<Page> pageSet, Set<Object> objSet){
        LocalDateTime endPoint = LocalDateTime.now().minusHours(24);
        for(Page currPage : pageSet){
            Set<PagePost> currPosts = pagePostRepository.findAllByPageTest(currPage, endPoint); // remove Test
            if(currPosts == null || currPosts.isEmpty())
                continue;

            objSet.addAll(currPosts);
        }
    }

    public Likes increaseLike(Long postId, User user) {
        Posts post = getById(postId);

        Likes newLike = new Likes();
        newLike.setPost(post);
        newLike.setUser(user);
        return likesRepository.save(newLike);
    }

    public Likes checkIfPostLiked(Long postId, User user) {
        Posts post = getById(postId);

        Likes isLiked = likesRepository.findLikeByPostAndUser(post, user);
        if(isLiked == null){ throw new LikeNotFoundException("Failed to find a like on the post."); }
        return isLiked;
    }

    public Boolean removePostLike(Long postId, User user) {
        likesRepository.deleteLikeByPostAndUser(postId, user);
        checkIfPostLiked(postId, user);

        return true;
    }
}