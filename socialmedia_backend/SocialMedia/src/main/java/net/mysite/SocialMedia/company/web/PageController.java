package net.mysite.SocialMedia.company.web;

import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.company.dto.PageDto;
import net.mysite.SocialMedia.company.sevice.PagePostPhotoService;
import net.mysite.SocialMedia.company.sevice.PageService;
import net.mysite.SocialMedia.company.sevice.PageThumbnailService;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.domain.UserPage;
import net.mysite.SocialMedia.err.MissingFieldException;
import net.mysite.SocialMedia.err.PageNotFoundException;
import net.mysite.SocialMedia.err.PageRoleNotFoundException;
import net.mysite.SocialMedia.err.UserNotFoundException;
import net.mysite.SocialMedia.service.UserPageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequestMapping("/api/pages")
public class PageController {

    private final Logger logger = LoggerFactory.getLogger(PagePostPhotoService.class);
    @Autowired
    private PageService pageService;
    @Autowired
    private PageThumbnailService pageThumbnailService;
    @Autowired
    private UserPageService userPageService;

    @PostMapping("")
    public ResponseEntity<?> createPage(@AuthenticationPrincipal User user, @RequestBody PageDto pageDto){
        try{
            Page createPage = pageService.save(user, pageDto);
            pageThumbnailService.save(createPage);
            userPageService.createUserPage(createPage, user);
            return ResponseEntity.ok(createPage);
        }
        catch (MissingFieldException e){
            logger.error(HttpStatus.BAD_REQUEST + ": error in page entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (PageNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in page entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("{pageId}/pageInfo")
    public ResponseEntity<?> getPageInfo(@PathVariable Long pageId){
        try{
            Page currPage = pageService.getById(pageId);
            return ResponseEntity.ok(currPage);
        }
        catch (PageNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in page entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("/userCreated")
    public ResponseEntity<?> getOwnerPages(@AuthenticationPrincipal User user){
        try {
            Set<Page> myPages = pageService.getMyPages(user);
            return ResponseEntity.ok(myPages);
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("{pageId}/userRole")
    public ResponseEntity<?> getsRole(@PathVariable Long pageId, @AuthenticationPrincipal User user){
        try{
            Page isPage = pageService.getById(pageId);
            UserPage isRole = userPageService.isUserPage(isPage, user);
            return ResponseEntity.ok(isRole);
        }
        catch (PageNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in page entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (PageRoleNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in pageRole entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @PostMapping("{pageId}/join")
    public ResponseEntity<?> joinsPage(@PathVariable Long pageId, @AuthenticationPrincipal User user){
        try{
            Page isPage = pageService.getById(pageId);
            UserPage newUser = userPageService.save(isPage, user);
            return ResponseEntity.ok(newUser);
        }
        catch (PageNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in page entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("/followingPages")
    public ResponseEntity<?> getListOfUsersFollowingPages(@AuthenticationPrincipal User user){
        try{
            Set<Page> followingPages = userPageService.generateListofUserFollowPage(user);
            return ResponseEntity.ok(followingPages);
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("/user/{userId}/followPages")
    public ResponseEntity<?> getFollowPagesListOtherUser(@PathVariable Long userId){
        try{
            Set<Page> followPages = userPageService.getListOfPagesUserFollow(userId);
            return ResponseEntity.ok(followPages);
        }
        catch (UserNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in user entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("{pageId}/followCount")
    public ResponseEntity<?> getPageFollowersCount(@PathVariable Long pageId){
        try{
            Long myPage = userPageService.getFollowCount(pageId);
            return ResponseEntity.ok(myPage);
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("/search")
    public ResponseEntity<?> getPagesOnGivenWord(@RequestParam("page") String searchTerm){
        try{
            Set<Page> pageSet = pageService.getPagesWithSearchTerm(searchTerm);
            return ResponseEntity.ok(pageSet);
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("/newPageList")
    public ResponseEntity<?> getPagesNotFollowedByUser(@AuthenticationPrincipal User user){
        try{
            Set<Page> pageSet = pageService.getSetOfPagesNotFollowedByUser(user);
            return ResponseEntity.ok(pageSet);
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @DeleteMapping("{pageId}/unfollow")
    public ResponseEntity<?> unfollowPageWithId(@PathVariable Long pageId, @AuthenticationPrincipal User user){
        try{
            userPageService.unfollowPage(pageId, user);
            return ResponseEntity.ok("Unfollowed page.");
        }
        catch (IllegalArgumentException e){
            logger.error(HttpStatus.BAD_REQUEST + ": error in page entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); }
    }
}
