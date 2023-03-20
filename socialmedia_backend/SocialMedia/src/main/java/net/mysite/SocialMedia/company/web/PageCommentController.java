package net.mysite.SocialMedia.company.web;

import net.mysite.SocialMedia.company.domain.PageComments;
import net.mysite.SocialMedia.company.domain.PagePost;
import net.mysite.SocialMedia.company.dto.PageCommentDto;
import net.mysite.SocialMedia.company.sevice.PageCommentService;
import net.mysite.SocialMedia.company.sevice.PagePostService;
import net.mysite.SocialMedia.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("")
    public ResponseEntity<?> createComment(@AuthenticationPrincipal User user,
                                           @RequestBody PageCommentDto pageCommentDto){

        PagePost pagePost = pagePostService.getById(pageCommentDto.getPagePost());

        PageComments pageComments = pageCommentService.save(pagePost, user, pageCommentDto);
        return ResponseEntity.ok(pageComments);
    }

    @GetMapping("{pagePostId}/comment")
    public ResponseEntity<?> getAllComments(@PathVariable Long pagePostId, @AuthenticationPrincipal User user){

        PagePost pagePost = pagePostService.getById(pagePostId);

        Set<PageComments> pageComments = pageCommentService.getAllCommentsOfPagePost(pagePost);
        return ResponseEntity.ok(pageComments);
    }
}
