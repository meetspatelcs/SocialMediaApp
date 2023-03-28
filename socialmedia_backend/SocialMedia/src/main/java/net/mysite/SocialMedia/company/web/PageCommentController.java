package net.mysite.SocialMedia.company.web;

import net.mysite.SocialMedia.company.domain.PageComments;
import net.mysite.SocialMedia.company.domain.PagePost;
import net.mysite.SocialMedia.company.dto.PageCommentDto;
import net.mysite.SocialMedia.company.sevice.PageCommentService;
import net.mysite.SocialMedia.company.sevice.PagePostPhotoService;
import net.mysite.SocialMedia.company.sevice.PagePostService;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.err.MissingFieldException;
import net.mysite.SocialMedia.err.PagePostNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequestMapping("/api/pageComments")
public class PageCommentController {

    @Autowired
    private PageCommentService pageCommentService;
    @Autowired
    private PagePostService pagePostService;
    private final Logger logger = LoggerFactory.getLogger(PagePostPhotoService.class);

    @PostMapping("")
    public ResponseEntity<?> createComment(@AuthenticationPrincipal User user,
                                           @RequestBody PageCommentDto pageCommentDto){
        try{
            PagePost pagePost = pagePostService.getById(pageCommentDto.getPagePost());
            PageComments pageComments = pageCommentService.save(pagePost, user, pageCommentDto);
            return ResponseEntity.ok(pageComments);
        }
        catch (PagePostNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in pagePost entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (MissingFieldException e){
            logger.error(HttpStatus.BAD_REQUEST + ": error in comment entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("{pagePostId}/comment")
    public ResponseEntity<?> getAllComments(@PathVariable Long pagePostId, @AuthenticationPrincipal User user){
        try{
            PagePost pagePost = pagePostService.getById(pagePostId);
            Set<PageComments> pageComments = pageCommentService.getAllCommentsOfPagePost(pagePost);
            return ResponseEntity.ok(pageComments);
        }
        catch (PagePostNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in pagePost entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }
}
