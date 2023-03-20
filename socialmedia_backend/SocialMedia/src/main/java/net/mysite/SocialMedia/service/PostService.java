package net.mysite.SocialMedia.service;
import net.bytebuddy.asm.Advice;
import net.mysite.SocialMedia.Enums.FriendRequestStatusEnum;
import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.company.domain.PagePost;
import net.mysite.SocialMedia.company.repository.PagePostRepository;
import net.mysite.SocialMedia.domain.Likes;
import net.mysite.SocialMedia.domain.Posts;
import net.mysite.SocialMedia.domain.PostsPhoto;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.errors.*;
import net.mysite.SocialMedia.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
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
        try{
            Posts post = new Posts();
            post.setUser(user);
            post.setCreationDate(LocalDateTime.now());
            post.setDescription(description);
            return postRepository.save(post);
        }
        catch (DataIntegrityViolationException e){
            throw new DatabaseException("Error saving Posts object to database. Unique constraint violation.");
        }
        catch (DataAccessException e){
            throw new DatabaseException("Error saving Posts object to the database.");
        }
    }
    public List<Posts> findByUser(User user){
        try {
            return postRepository.findByUser(user);
        }
        catch (DataAccessException e){
            String message = "Error retrieving posts for user: " + user.getId() + ": " + e.getMessage();
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
    }
    public Posts getById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));
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
    public void deleteById(Long delId) {
        try{
            postRepository.deleteById(delId);
        }
        catch (EmptyResultDataAccessException e){
            throw new EmptyResultDataAccessException("Post with ID " + delId + " does not exist", 1);
        }
    }
    public Posts updatePost(Long postId, String description, MultipartFile myFile, User user) {

        Posts myPost = getById(postId);
        if(description != null){
            updatePostDescription(myPost, description);
            postRepository.save(myPost);
        }
        if(myFile != null){
            PostsPhoto updateMedia = postsPhotoRepository.findById(postId)
                    .orElseThrow(() -> new ResourceNotFoundException("Post media not found with ID: " + postId));
            updatePostMedia(updateMedia, myFile, user);
            postsPhotoRepository.save(updateMedia);
        }
        return myPost;
    }
    private void updatePostDescription(Posts post, String description){
        post.setDescription(description);
    }
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
        if(!userRepository.existsById(userId)){
            throw new UserNotFoundException("User with ID: " + userId + " does not exists.");
        }
        return postRepository.getByuserId(userId);
    }
    public Set<Object> getTodayPostsByFriendAndPage(User user) {
        Set<Object> dashboardPosts = generatePostsOfFriendsAndPages(user);
        return dashboardPosts;
    }
    private Set<Object> generatePostsOfFriendsAndPages(User user){
        Set<User> myFriendSet = convertFriendToUser(user);
        Set<Object> resPosts = new HashSet<>();
        if(myFriendSet == null || myFriendSet.size() == 0){
            loggedUserPosts(user, resPosts);
            return resPosts;
        }
        generateUsersPosts(myFriendSet, resPosts);

        Set<Page> pageSet = userPageRepository.findPageWithUser(user, "ROLE_USER");
        generatePagePosts(pageSet, resPosts);
        return resPosts;
    }
    private void loggedUserPosts(User user, Set<Object> objSet){
        LocalDateTime endPoint = LocalDateTime.now().minusHours(24);
        List<Posts> currUserPost = postRepository.findPostWithTimeByUser(user, endPoint);
        if(currUserPost.size() == 0 || currUserPost == null)
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
        try{
            return likesRepository.save(newLike);
        }
        catch (Exception e){
            logger.error("Failed to save new like for post with id: " + postId);
            throw new RuntimeException("An error occurred. Failed to save new like.");
        }
    }
    public Likes checkIfPostLiked(Long postId, User user) {
        Posts post = getById(postId);
        try{
            return likesRepository.findLikeByPostAndUser(post, user);
        }
        catch (NotFoundException e){
            throw new NotFoundException("No like on post by user.");
        }
    }
    public Boolean removePostLike(Long postId, User user) {
        likesRepository.deleteLikeByPostAndUser(postId, user);
        Likes like = checkIfPostLiked(postId, user);

        if(like != null){
            throw new RuntimeException("Failed to remove like.");
        }
        return true;
    }
}
