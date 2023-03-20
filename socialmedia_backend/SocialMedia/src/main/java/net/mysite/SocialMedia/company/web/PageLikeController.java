package net.mysite.SocialMedia.company.web;

import net.mysite.SocialMedia.company.domain.PageLikes;
import net.mysite.SocialMedia.company.domain.PagePost;
import net.mysite.SocialMedia.company.sevice.PageLikeService;
import net.mysite.SocialMedia.company.sevice.PagePostService;
import net.mysite.SocialMedia.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/pagePost/{pagePostId}/increaseLike")
    public ResponseEntity<?> increaseLikeOnPagePost(@PathVariable Long pagePostId, @AuthenticationPrincipal User user){

        PagePost pagePost = pagePostService.getById(pagePostId);
        PageLikes pageLikes = pageLikeService.increasePagePostLike(pagePost, user);

        return ResponseEntity.ok(pageLikes);
    }

    @GetMapping("/pagePost/{pagePostId}/isLiked")
    public ResponseEntity<?> getIsPagePostLiked(@PathVariable Long pagePostId, @AuthenticationPrincipal User user){
        PagePost pagePost = pagePostService.getById(pagePostId);
        PageLikes isLiked = pageLikeService.checkIfPagePostIsLiked(pagePost, user);

        return ResponseEntity.ok(isLiked);
    }

    @DeleteMapping("/pagePost/{pagePostId}/decreaseLike")
    public ResponseEntity<?> decreaseLikeOnPagePost(@PathVariable Long pagePostId, @AuthenticationPrincipal User user){
        Boolean removedLike = pageLikeService.decreasePagePostLike(pagePostId, user);

        return ResponseEntity.ok(removedLike);
    }
}
