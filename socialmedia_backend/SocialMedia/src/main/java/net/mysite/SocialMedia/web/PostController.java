package net.mysite.SocialMedia.web;

import net.mysite.SocialMedia.domain.Likes;
import net.mysite.SocialMedia.domain.Posts;
import net.mysite.SocialMedia.domain.PostsPhoto;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.errors.*;
import net.mysite.SocialMedia.service.PostService;
import net.mysite.SocialMedia.service.PostsPhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    @Autowired
    private PostService postService;
    @Autowired
    private PostsPhotoService postsPhotoService;
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";

    @PostMapping(value = "", consumes = {MULTIPART_FORM_DATA})
    public ResponseEntity<?> createPost(@RequestParam(value = "myFile", required = false) MultipartFile myFile,
                                            @RequestParam(value = "description", required = true) String description,
                                            @AuthenticationPrincipal User user) {
        try{
            if(description == null || description.trim().isEmpty()){
                throw new InvalidDescriptionException("Description cannot be null or empty.");
            }

            Posts newPost = postService.save(description, user);
            PostsPhoto newPostImg = postsPhotoService.save(newPost, user, myFile);
            return ResponseEntity.ok(newPost);
        }
        catch (InvalidDescriptionException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the post.");
        }
    }

    @PutMapping(value = "{postId}", consumes = {MULTIPART_FORM_DATA})
    public ResponseEntity<?> updatePost(@RequestParam(value="myFile", required = false) MultipartFile myFile,
                                        @RequestParam(value = "description", required = false) String description,
                                        @PathVariable Long postId, @AuthenticationPrincipal User user) {
        try{
            if(description == null && myFile == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Both description and media cannot be empty or null.");
            }
            if(postId <= 0){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            if(myFile != null && !isValidFileFormat(myFile)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            Posts updatePost = postService.updatePost(postId, description, myFile, user);
            return ResponseEntity.ok(updatePost);
        }
        catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (DataAccessException e){
            logger.error("Error accessing database: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        catch (PostNotFoundException e){
            logger.error("Post not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardPosts(@AuthenticationPrincipal User user){
        try{
            Set<Object> todayPosts = postService.getTodayPostsByFriendAndPage(user);
            return ResponseEntity.ok(todayPosts);
        }
        catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching dashboard posts");
        }
    }

    @GetMapping("/myPosts")
    public ResponseEntity<?> getMyPosts(@AuthenticationPrincipal User user){
        try {
            List<Posts> myPosts = postService.findByUser(user);
            return ResponseEntity.ok(myPosts);
        }
        catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found.");
        }
        catch (RuntimeException e){
            String message = "Error retrieving posts for user: " + user.getId() + ": " + e.getMessage();
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

//    @GetMapping("{postId}")
//    public ResponseEntity<?> getPostById(@PathVariable Long postId){
//        try {
//            Posts getPost = postService.getById(postId);
//            return ResponseEntity.ok(getPost);
//        }
//        catch (PostNotFoundException e){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
//        catch (Exception e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
//        }
//    }

    @GetMapping("{postId}/postPhotos")
    public ResponseEntity<?> getImgOfPost(@PathVariable Long postId){
        try{
            PostsPhoto postImg = postsPhotoService.getById(postId);
            return ResponseEntity.ok(postImg);
        }
        catch (NoSuchElementException e){
            String message = "Post image with ID: " + postId + " not found.";
            logger.error(message, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        catch (IllegalArgumentException e){
            String message = "Invalid request parameter";
            logger.error(message, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found.");
        }
        catch (Exception e){
            String message = "Error retrieving post image metadata";
            logger.error(message, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
        }
    }

    @DeleteMapping("{delId}")
    public ResponseEntity<?> deletePostById(@PathVariable Long delId){
        try{
            postsPhotoService.deleteById(delId);
            postService.deleteById(delId);
            return ResponseEntity.ok("Post has been deleted!");
        }
        catch (DataIntegrityViolationException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        catch (EmptyResultDataAccessException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("{postId}/postImages")
    public ResponseEntity<?> getUserPostImg(@PathVariable Long postId){
        try{
            byte[] imgByte = postsPhotoService.getImgById(postId);
            return ResponseEntity.ok(imgByte);
        }
        catch (NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("postId is null");
        }
        catch (ImageNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            logger.error("Failed to retrieve image for postId: ", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve image for postId: " + postId);
        }
    }

    @GetMapping("{postId}/postVideos")
    public Mono<ResponseEntity<?>> getUserPostVideo(@PathVariable Long postId, @RequestHeader(value = "range", required  = false) String httpRangeList){
        try{
            return Mono.just(postsPhotoService.getVideoById(postId, httpRangeList));
        }
        catch (IOException e){
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

    @GetMapping("/myPosts/imgList")
    public ResponseEntity<?> getImgListFromMyPosts(@AuthenticationPrincipal User user){
        try{
            Set<PostsPhoto> imgInfoSet = postsPhotoService.findAllImg(user);
            return ResponseEntity.ok(imgInfoSet);
        }
        catch (DataAccessException e){
            logger.error("Error retrieving image list from user's posts.",  e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving image list from user's posts." + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/profile")
    public ResponseEntity<?> getVisitUserPosts(@PathVariable Long userId){
        try {
            List<Posts> visitUserPosts = postService.findByUserHandler(userId);
            if(visitUserPosts.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(visitUserPosts);
        }
        catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving posts for user: " + userId);
        }
    }

    @GetMapping("/user/{userId}/profile/imgList")
    public ResponseEntity<?> getVisitUserImgListFromPosts(@PathVariable Long userId){
        try{
            Set<PostsPhoto> visitUserImgInfoSet = postsPhotoService.findAllImgHandler(userId);
            if(visitUserImgInfoSet.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(visitUserImgInfoSet);
        }
        catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @PostMapping("{postId}/increaseLike")
    public ResponseEntity<?> increasePostLike(@PathVariable Long postId, @AuthenticationPrincipal User user){
        try{
            Likes likes = postService.increaseLike(postId, user);
            likes.setUser(null);
            likes.setPost(null);
            return ResponseEntity.ok(likes);
        }
        catch (PostNotFoundException e){
            logger.error(HttpStatus.BAD_REQUEST +  e.getMessage() + " Failed to like on post.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e){
            logger.error("Failed to  like post with ID " + postId + ". An error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @GetMapping("{postId}/isLiked")
    public ResponseEntity<?> getIsPostLiked(@PathVariable Long postId, @AuthenticationPrincipal User user){
        try{
            Likes isLiked = postService.checkIfPostLiked(postId, user);
            isLiked.setPost(null);
            isLiked.setUser(null);
            return ResponseEntity.ok(isLiked);
        }
        catch (PageNotFoundException e){
            logger.error(HttpStatus.BAD_REQUEST +  e.getMessage() + " Failed to find like on post.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (NotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + "No like was found on post: " + postId + " by user: " + user.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            logger.error("Failed to find like on post with ID " + postId + ". An error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @DeleteMapping("{postId}/decreaseLike")
    public ResponseEntity<?> removeLike(@PathVariable Long postId, @AuthenticationPrincipal User user){
        try{
            Boolean removedLike = postService.removePostLike(postId, user);
            return ResponseEntity.ok(removedLike);
        }
        catch (Exception e){
            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + ": an error occurred. Failed to remove like.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred. Failed to remove like.");
        }
    }

    private boolean isValidFileFormat(MultipartFile file){
        // TODO: create a logic to validate file such as img, vid, etc.
        return true;
    }
}
