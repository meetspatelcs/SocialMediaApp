package net.mysite.SocialMedia.web;

import net.mysite.SocialMedia.domain.Comment;
import net.mysite.SocialMedia.domain.Posts;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.dto.CommentDto;
import net.mysite.SocialMedia.err.MissingFieldException;
import net.mysite.SocialMedia.service.CommentService;
import net.mysite.SocialMedia.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private PostService postService;
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @PostMapping("")
    public ResponseEntity<?> createComment(@AuthenticationPrincipal User user,
                                           @RequestBody CommentDto commentDto){
        try{
            Posts post = postService.getById(commentDto.getPost());
            Comment newComment = commentService.createComment(post, user, commentDto);
            return ResponseEntity.ok(newComment);
        }
        catch (NullPointerException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in post entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (MissingFieldException e){
            logger.error(HttpStatus.BAD_REQUEST + ": error in comment entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("{postId}/comment")
    public ResponseEntity<?> getAllComments(@PathVariable Long postId,
                                            @AuthenticationPrincipal User user){
        try{
            Posts post = postService.getById(postId);
            Set<Comment> commentSet = commentService.getAllCommentsOfPost(post);
            return ResponseEntity.ok(commentSet);
        }
        catch (NullPointerException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in post entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }
}
