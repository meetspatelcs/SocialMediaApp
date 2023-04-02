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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.mysite.SocialMedia.company.constants.PageConstants.default_join;

@Service
public class PostServiceImpl implements PostService{

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

    @Override
    public Posts save(String description, User user) throws MissingFieldException {
        if(description.isEmpty()){ throw new MissingFieldException("Description cannot be null or empty."); }

        Posts post = new Posts();
        post.setUser(user);
        post.setCreationDate(LocalDateTime.now());
        post.setDescription(description);
        return postRepository.save(post);
    }

    @Override
    public Posts getById(Long postId) throws NullPointerException {
        return postRepository.findById(postId).
                orElseThrow(() -> new NullPointerException("Post was not found."));
    }

    @Override
    public List<Posts> findByUser(User user){
        List<Posts> userPostSet = postRepository.findByUser(user);
        if(userPostSet.isEmpty()) {return Collections.emptyList(); }
        return userPostSet;
    }

    @Override
    public void deleteById(Long delId) { postRepository.deleteById(delId); }

    @Override
    public Posts updatePost(Long postId, String description, MultipartFile myFile, User user) throws NullPointerException, PostMediaNotFoundException {
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

    @Override
    public List<Posts> findByUserHandler(Long userId) throws UserNotFoundException {
        if(!userRepository.existsById(userId)){ throw new UserNotFoundException("User does not exists."); }

        List<Posts> userPostList = postRepository.getByuserId(userId);
        if(userPostList.isEmpty()){ return Collections.emptyList(); }
        return userPostList;
    }

    @Override
    public Set<Object> getTodayPostsByFriendAndPage(User user) {
        Set<Object> dashboardPosts = generatePostsOfFriendsAndPages(user);
        return dashboardPosts;
    }

    @Override
    public Likes increaseLike(Long postId, User user) throws NullPointerException{
        Posts post = getById(postId);

        Likes newLike = new Likes();
        newLike.setPost(post);
        newLike.setUser(user);
        return likesRepository.save(newLike);
    }

    @Override
    public Likes checkIfPostLiked(Long postId, User user) throws LikeNotFoundException {
        Posts post = getById(postId);

        Likes isLiked = likesRepository.findLikeByPostAndUser(post, user);
        if(isLiked == null){ throw new LikeNotFoundException("Failed to find a like on the post."); }
        return isLiked;
    }

    @Override
    public Boolean removePostLike(Long postId, User user) {
        likesRepository.deleteLikeByPostAndUser(postId, user);
        checkIfPostLiked(postId, user);

        return true;
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

    private void updatePostDescription(Posts post, String description){ post.setDescription(description); }

    private void updatePostMedia(PostsPhoto updateMedia, MultipartFile myFile, User user) {
        try{
            postsPhotoService.mediaUpdateHelper(updateMedia, user, myFile);
        }
        catch (IOException e){ throw new RuntimeException(); }
    }

    private Set<Object> generatePostsOfFriendsAndPages(User user){
        Set<User> myFriendSet = convertFriendToUser(user);
        Set<Object> resPosts = new HashSet<>();
        if(!myFriendSet.isEmpty()){ generateUsersPosts(myFriendSet, resPosts); }
        loggedUserPosts(user, resPosts);
        Set<Page> pageSet = userPageRepository.findPageWithUser(user, default_join);
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
}