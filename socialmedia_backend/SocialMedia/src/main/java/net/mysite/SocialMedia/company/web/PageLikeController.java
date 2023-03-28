package net.mysite.SocialMedia.company.web;

import net.mysite.SocialMedia.company.domain.PageLikes;
import net.mysite.SocialMedia.company.domain.PagePost;
import net.mysite.SocialMedia.company.sevice.PageLikeService;
import net.mysite.SocialMedia.company.sevice.PagePostPhotoService;
import net.mysite.SocialMedia.company.sevice.PagePostService;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.err.PagePostLikeException;
import net.mysite.SocialMedia.err.PagePostLikeNotFoundException;
import net.mysite.SocialMedia.err.PagePostNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pageLikes")
public class PageLikeController {

    @Autowired
    private PageLikeService pageLikeService;
    @Autowired
    private PagePostService pagePostService;
    private final Logger logger = LoggerFactory.getLogger(PagePostPhotoService.class);

    @PostMapping("/pagePost/{pagePostId}/increaseLike")
    public ResponseEntity<?> increaseLikeOnPagePost(@PathVariable Long pagePostId, @AuthenticationPrincipal User user){
        try{
            PagePost pagePost = pagePostService.getById(pagePostId);
            PageLikes pageLikes = pageLikeService.increasePagePostLike(pagePost, user);
            return ResponseEntity.ok(pageLikes);
        }
        catch (PagePostNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in pagePost entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("/pagePost/{pagePostId}/isLiked")
    public ResponseEntity<?> getIsPagePostLiked(@PathVariable Long pagePostId, @AuthenticationPrincipal User user){
        try{
            PagePost pagePost = pagePostService.getById(pagePostId);
            PageLikes isLiked = pageLikeService.checkIfPagePostIsLiked(pagePost, user);
            return ResponseEntity.ok(isLiked);
        }
        catch (PagePostNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in pagePost entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (PagePostLikeNotFoundException e){
            logger.error(": error in Page Likes entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @DeleteMapping("/pagePost/{pagePostId}/decreaseLike")
    public ResponseEntity<?> decreaseLikeOnPagePost(@PathVariable Long pagePostId, @AuthenticationPrincipal User user){
        try{
            Boolean removedLike = pageLikeService.decreasePagePostLike(pagePostId, user);
            return ResponseEntity.ok(removedLike);
        }
        catch (PagePostLikeException e){
            logger.error(HttpStatus.CONFLICT + ": error in pageLike entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }
}
