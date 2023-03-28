package net.mysite.SocialMedia.web;

import net.mysite.SocialMedia.domain.Likes;
import net.mysite.SocialMedia.domain.Posts;
import net.mysite.SocialMedia.domain.PostsPhoto;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.err.LikeNotFoundException;
import net.mysite.SocialMedia.err.MissingFieldException;
import net.mysite.SocialMedia.err.PostMediaNotFoundException;
import net.mysite.SocialMedia.err.UserNotFoundException;
import net.mysite.SocialMedia.service.PostService;
import net.mysite.SocialMedia.service.PostsPhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import java.io.IOException;
import java.util.List;
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
            Posts newPost = postService.save(description, user);
            PostsPhoto newPostImg = postsPhotoService.save(newPost, user, myFile); // creates empty post media
            return ResponseEntity.ok(newPost);
        }
        catch (MissingFieldException e){
            logger.error(HttpStatus.BAD_REQUEST + ": error in Post entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @PutMapping(value = "{postId}", consumes = {MULTIPART_FORM_DATA})
    public ResponseEntity<?> updatePost(@RequestParam(value="myFile", required = false) MultipartFile myFile,
                                        @RequestParam(value = "description", required = false) String description,
                                        @PathVariable Long postId, @AuthenticationPrincipal User user) {
        try{
            // TODO: if both are null, user wants to remove img, update the following code
            if(description == null && myFile == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Both description and media cannot be empty or null.");
            }
            if(postId <= 0){ return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); }
            if(myFile != null && !isValidFileFormat(myFile)){ return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); }

            Posts updatePost = postService.updatePost(postId, description, myFile, user);
            return ResponseEntity.ok(updatePost);
        }
        catch (NullPointerException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in post entity." + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (PostMediaNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in postPhoto entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardPosts(@AuthenticationPrincipal User user){
        try{
            Set<Object> todayPosts = postService.getTodayPostsByFriendAndPage(user);
            return ResponseEntity.ok(todayPosts); // sends empty set, if not found any
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("/myPosts")
    public ResponseEntity<?> getMyPosts(@AuthenticationPrincipal User user){
        try {
            List<Posts> myPosts = postService.findByUser(user);
            return ResponseEntity.ok(myPosts); // sends empty list, if not found any
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
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
        catch (PostMediaNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in postPhoto entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @DeleteMapping("{delId}")
    public ResponseEntity<?> deletePostById(@PathVariable Long delId){
        try{
            postsPhotoService.deleteById(delId);
            postService.deleteById(delId);
            return ResponseEntity.ok("Post has been deleted!");
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("{postId}/postImages")
    public ResponseEntity<?> getUserPostImg(@PathVariable Long postId){
        try{
            byte[] imgByte = postsPhotoService.getImgById(postId);
            return ResponseEntity.ok(imgByte);
        }
        catch (PostMediaNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in postPhoto entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (NullPointerException e){
            logger.error(HttpStatus.NO_CONTENT + ": error in postPhoto entity." + e.getMessage());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        }
        catch (IOException e){
            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + ": error in postPhoto entity. " +  "Something went wrong.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("{postId}/postVideos")
    public Mono<ResponseEntity<?>> getUserPostVideo(@PathVariable Long postId,
                                                    @RequestHeader(value = "range", required  = false) String httpRangeList){
        try{
            return Mono.just(postsPhotoService.getVideoById(postId, httpRangeList));
        }
        catch (PostMediaNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in postPhoto entity. " + e.getMessage());
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()));
        }
        catch (NullPointerException e){
            logger.error(HttpStatus.NO_CONTENT + ": error in postPhoto entity. " + e.getMessage());
            return Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage()));
        }
        catch (IOException e){ return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()); }
    }

    @GetMapping("/myPosts/imgList")
    public ResponseEntity<?> getImgListFromMyPosts(@AuthenticationPrincipal User user){
        try{
            Set<PostsPhoto> imgInfoSet = postsPhotoService.findAllImg(user);
            return ResponseEntity.ok(imgInfoSet); // sends empty set, if not found any
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("/user/{userId}/profile")
    public ResponseEntity<?> getVisitUserPosts(@PathVariable Long userId){
        try {
            List<Posts> visitUserPosts = postService.findByUserHandler(userId);
            return ResponseEntity.ok(visitUserPosts); // sends empty list, if not found any
        }
        catch (UserNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in user entity." + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("/user/{userId}/profile/imgList")
    public ResponseEntity<?> getVisitUserImgListFromPosts(@PathVariable Long userId){
        try{
            Set<PostsPhoto> visitUserImgInfoSet = postsPhotoService.findAllImgHandler(userId);
            return ResponseEntity.ok(visitUserImgInfoSet); // sends empty list, if not found any
        }
        catch (UserNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in user entity." + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @PostMapping("{postId}/increaseLike")
    public ResponseEntity<?> increasePostLike(@PathVariable Long postId, @AuthenticationPrincipal User user){
        try{
            Likes likes = postService.increaseLike(postId, user);
            likes.setUser(null);
            likes.setPost(null);
            return ResponseEntity.ok(likes);
        }
        catch (NullPointerException e){
            logger.error(HttpStatus.BAD_REQUEST + ": error in like entity. " + e.getMessage() + " Failed to like on post.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("{postId}/isLiked")
    public ResponseEntity<?> getIsPostLiked(@PathVariable Long postId, @AuthenticationPrincipal User user){
        try{
            Likes isLiked = postService.checkIfPostLiked(postId, user);
            isLiked.setPost(null);
            isLiked.setUser(null);
            return ResponseEntity.ok(isLiked);
        }
        catch (NullPointerException e){
            logger.error(HttpStatus.BAD_REQUEST + ": error in like entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (LikeNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in like entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @DeleteMapping("{postId}/decreaseLike")
    public ResponseEntity<?> removeLike(@PathVariable Long postId, @AuthenticationPrincipal User user){
        try{
            Boolean removedLike = postService.removePostLike(postId, user);
            return ResponseEntity.ok(removedLike);
        }
        catch (LikeNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in like entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    private boolean isValidFileFormat(MultipartFile file){
        // TODO: create a logic to validate file such as img, vid, etc.
        return true;
    }
}