package net.mysite.SocialMedia.company.web;

import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.company.domain.PagePost;
import net.mysite.SocialMedia.company.domain.PagePostPhoto;
import net.mysite.SocialMedia.company.dto.PageDto;
import net.mysite.SocialMedia.company.sevice.PagePostPhotoService;
import net.mysite.SocialMedia.company.sevice.PagePostService;
import net.mysite.SocialMedia.company.sevice.PageService;
import net.mysite.SocialMedia.company.sevice.PageThumbnailService;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.domain.UserPage;
import net.mysite.SocialMedia.errors.*;
import net.mysite.SocialMedia.service.UserPageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/api/pages")
public class PageController {
    private final Logger logger = LoggerFactory.getLogger(PagePostPhotoService.class);
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    @Autowired
    private PageService pageService;
    @Autowired
    private PageThumbnailService pageThumbnailService;
    @Autowired
    private PagePostPhotoService pagePostPhotoService;
    @Autowired
    private PagePostService pagePostService;
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
        catch (InvalidInputException e){
            String errMsg = "Failed to create page. " + e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errMsg);
        }
        catch (ThumbnailCreationException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        catch (UserPageCreationException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred. Failed to create page.");
        }
    }

    @GetMapping("{pageId}/pageInfo")
    public ResponseEntity<?> getPageInfo(@PathVariable Long pageId){
        try{
            Page currPage = pageService.getById(pageId);
            return ResponseEntity.ok(currPage);
        }
        catch (PageNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @GetMapping("/userCreated")
    public ResponseEntity<?> getOwnerPages(@AuthenticationPrincipal User user){
        try {
            Set<Page> myPages = pageService.getMyPages(user);
            return ResponseEntity.ok(myPages);
        }
        catch (PageNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

//    @GetMapping("{pageId}/thumbnails")
//    public ResponseEntity<?> getPageThumbnail(@AuthenticationPrincipal User user, @PathVariable Long pageId){
//        try{
//            byte[] thumbnail = pageThumbnailService.getThumbnail(pageId);
//            return ResponseEntity.ok(thumbnail);
//        }
//        catch (NullPointerException e){
//            logger.error("Bad Request: thumbnail img was not found for page " + pageId);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//        catch (ImageNotFoundException e){
//            logger.error("Error 404: Thumbnail was not found in entity PageThumbnail.");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
//        catch (Exception e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
//        }
//    }

//    @PutMapping(value = "{pageId}/thumbnails", consumes = {MULTIPART_FORM_DATA})
//    public ResponseEntity<?> setThumbnails(@PathVariable Long pageId,
//                                           @RequestParam(value = "myFile", required = false) MultipartFile myFile,
//                                           @AuthenticationPrincipal User user) throws IOException {
//        try{
//            PageThumbnail updatePageThumbnail = pageThumbnailService.save(pageId, myFile, user);
//            return ResponseEntity.ok(updatePageThumbnail);
//        }
//        catch (FileNotFoundException e){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
//        catch (IOException e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Io error.");
//        }
//        catch (Exception e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
//        }
//    }

//    @PostMapping(value = "/{pageId}/createPagePost", consumes = {MULTIPART_FORM_DATA})
//    public ResponseEntity<?> createNewPostOnPage(@PathVariable Long pageId,
//                                                 @RequestParam(value = "myFile", required = false) MultipartFile myFile,
//                                                 @RequestParam(value = "description", required = true) String description,
//                                                 @AuthenticationPrincipal User user) throws IOException {
//
//        try{
//            if(pageId == null || description.trim().isEmpty()){
//                throw new InvalidDescriptionException("Description cannot be null or empty.");
//            }
//
//            PagePost newPagePost = pagePostService.save(pageId, description);
//            PagePostPhoto newPPostPhoto = pagePostPhotoService.save(newPagePost, myFile, user);
//
//            return ResponseEntity.ok(newPPostPhoto);
//        }
//        catch (IOException e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//        }
//        catch (InvalidDescriptionException e){
//            logger.error(HttpStatus.BAD_REQUEST + ": description can not be empty for page post.");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//        catch (DataAccessException e){
//            logger.error(HttpStatus.BAD_REQUEST + ": failed to create post for page: " + pageId);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//        catch (Exception e){
//            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + ": an error occurred.");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
//        }
//    }

//    @GetMapping("{pageId}/allPosts")
//    public ResponseEntity<?> getAllPagePosts(@PathVariable Long pageId){
//        try{
//            Page myPage = pageService.getById(pageId);
//            Set<PagePost> allPagePosts = pagePostService.getAllByPage(myPage);
//            return ResponseEntity.ok(allPagePosts);
//        }
//        catch (PageNotFoundException e){
//            logger.error(HttpStatus.NOT_FOUND + ":" + "Page with ID " + pageId + " not found in Entity Page");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
//        catch (NullPointerException e){
//            logger.error(HttpStatus.NOT_FOUND + ": No posts are found in entity page With ID " + pageId);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
//        catch (Exception e){
//            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + "An error occurred.");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
//        }
//    }

////    TODO: try catch below, not done all catches
//    @DeleteMapping("{pageId}/posts/{postId}")
//    public ResponseEntity<?> removePostOnPage(@PathVariable Long pageId, @PathVariable Long postId){
//
//        try{
//            pagePostService.removeById(pageId, postId);
//            return ResponseEntity.ok("Post has been deleted!");
//        }
//        catch (Exception e){
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

//    @GetMapping("{pageId}/posts/{postId}")
//    public ResponseEntity<?> getPagePostById(@PathVariable Long pageId, @PathVariable Long postId){
//        try{
//            PagePostPhoto myPost = pagePostPhotoService.getById(pageId,  postId);
//            return ResponseEntity.ok(myPost);
//        }
//        catch (PostNotFoundException e){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
//        catch (Exception e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
//        }
//    }

//    @PutMapping(value = "{pageId}/posts/{postId}", consumes = {MULTIPART_FORM_DATA})
//    public ResponseEntity<?> updatePagePostById(@PathVariable Long pageId,
//                                                @PathVariable Long postId,
//                                                @RequestParam(value = "myFile", required = false) MultipartFile myFile,
//                                                @RequestParam(value = "description", required = true) String description,
//                                                @AuthenticationPrincipal User user) throws IOException {
//
//        try{
//            PagePost updatePost = pagePostService.UpdatePagePostById(postId, description);
//            PagePostPhoto pagePostPhoto = pagePostPhotoService.getById(pageId, postId);
//
//            if(pagePostPhoto != null && myFile != null){ pagePostPhotoService.updateSave(pagePostPhoto, myFile, user);}
//
//            return ResponseEntity.ok(pagePostPhoto);
//        }
//        catch (IOException e){
//            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + ": an error occurred.");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
//        }
//        catch (PostNotFoundException e){
//            logger.error(HttpStatus.NOT_FOUND + ": post was not found on page: " + pageId);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
//        catch (Exception e){
//            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + ": an error occurred.");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
//        }
//    }

//    @GetMapping("{pageId}/pagePostTemp/{pagePostId}")
//    public Mono<ResponseEntity<?>> getVideo(@PathVariable Long pageId,
//                                            @PathVariable Long pagePostId,
//                                            @RequestHeader(value = "range", required  = false) String httpRangeList)  {
//        try{
//            return Mono.just(pagePostPhotoService.getVideoById(pageId, pagePostId, httpRangeList));
//        }
//        catch (IOException e){
//            logger.error(HttpStatus.BAD_REQUEST + "Something went wrong while fetching video for page.");
//            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
//        }
//    }

//    @GetMapping("{pageId}/postsTemp/{postId}")
//    public ResponseEntity<?> getImg(@PathVariable Long pageId, @PathVariable Long postId) throws IOException {
//        try{
//            byte[] imgBuff = pagePostPhotoService.getImgById(pageId, postId);
//            return ResponseEntity.ok(imgBuff);
//        }
//        catch (NullPointerException e){
//            logger.error(HttpStatus.BAD_REQUEST + "img was not for post with ID " + postId + " in PagePost entity.");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//        catch (ImageNotFoundException e){
//            logger.error(HttpStatus.NOT_FOUND + ": post was not found in entity pagePost.");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
//        catch (Exception e){
//            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + ": an error occurred in pagePost entity.");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
//        }
//    }

    @GetMapping("{pageId}/userRole")
    public ResponseEntity<?> getsRole(@PathVariable Long pageId, @AuthenticationPrincipal User user){
        try{
            Page isPage = pageService.getById(pageId);
            UserPage isRole = userPageService.isUserPage(isPage, user);
            return ResponseEntity.ok(isRole);
        }
        catch (PageNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": Failed to find Page in Entity Page with ID " + pageId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (NotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": Failed to find a role in Entity UserPage with page ID " + pageId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + "An error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @PostMapping("{pageId}/join")
    public ResponseEntity<?> joinsPage(@PathVariable Long pageId, @AuthenticationPrincipal User user){
        try{
            Page isPage = pageService.getById(pageId);
            UserPage newUser = userPageService.save(isPage, user);

            return ResponseEntity.ok(newUser);
        }
        catch (PageNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": no page was found to join.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (IllegalArgumentException e){
            logger.error(HttpStatus.BAD_REQUEST + ": failed to join a page.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e){
            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + ": an error occurred while joining a page.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @GetMapping("/followingPages")
    public ResponseEntity<?> getListOfUsersFollowingPages(@AuthenticationPrincipal User user){
        try{
            Set<Page> followingPages = userPageService.generateListofUserFollowPage(user);
            return ResponseEntity.ok(followingPages);
        }
        catch (NotFoundException e){
            logger.error(HttpStatus.NO_CONTENT + ": Not following any pages.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        }
        catch (Exception e){
            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + ": An error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @GetMapping("/user/{userId}/followPages")
    public ResponseEntity<?> getFollowPagesListOtherUser(@PathVariable Long userId){
        try{
            Set<Page> followPages = userPageService.getListOfPagesUserFollow(userId);
            return ResponseEntity.ok(followPages);
        }
        catch (NotFoundException e){
            logger.error(HttpStatus.NO_CONTENT + ": NO new pages to follow.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        }
        catch (Exception e){
            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + "An error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

//    @GetMapping("{pageId}/allMediaInfo/{mediaType}")
//    public ResponseEntity<?> generateMediaInfoList(@PathVariable Long pageId, @PathVariable String mediaType){
//        try{
//            Page myPage = pageService.getById(pageId);
//            Set<PagePostPhoto> mediaInfoList = pagePostService.getMediaInfoList(myPage, mediaType);
//            return ResponseEntity.ok(mediaInfoList);
//        }
//        catch (PageNotFoundException e){
//            logger.error(HttpStatus.NOT_FOUND + ": Page not found in entity Page, ID " + pageId);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
//        catch (NullPointerException e){
//            logger.error(HttpStatus.NOT_FOUND + ": No Page Post Photo content.");
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
//        }
//        catch (Exception e){
//            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + ": An error occurred.");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
//        }
//    }

    @GetMapping("{pageId}/followCount")
    public ResponseEntity<?> getPageFollowersCount(@PathVariable Long pageId){
        try{
            Long myPage = userPageService.getFollowCount(pageId);
            return ResponseEntity.ok(myPage);
        }
        catch (Exception e){
            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + ": An error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> getPagesOnGivenWord(@RequestParam("page") String searchTerm){
        try{
            Set<Page> pageSet = pageService.getPagesWithSearchTerm(searchTerm);
            return ResponseEntity.ok(pageSet);
        }
        catch (NotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": No page found in Page entity with search term: " + searchTerm);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + ": An error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @GetMapping("/newPageList")
    public ResponseEntity<?> getPagesNotFollowedByUser(@AuthenticationPrincipal User user){
        try{
            Set<Page> pageSet = pageService.getSetOfPagesNotFollowedByUser(user);
            return ResponseEntity.ok(pageSet);
        }
        catch (NullPointerException e){
            logger.error(HttpStatus.BAD_REQUEST + ": There are no new pages to follow.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e){
            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + ": An error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @DeleteMapping("{pageId}/unfollow")
    public ResponseEntity<?> unfollowPageWithId(@PathVariable Long pageId, @AuthenticationPrincipal User user){
        try{
            userPageService.unfollowPage(pageId, user);
            return ResponseEntity.ok("Unfollowed page.");
        }
        catch (IllegalArgumentException e){
            logger.error(HttpStatus.BAD_REQUEST + ": Page and user must not be null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (DataAccessException e){
            logger.error(HttpStatus.BAD_REQUEST + ": User is not following this page.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e){
            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + "An error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @PutMapping("{pageId}/thumbnails/remove")
//    public ResponseEntity<?> removePageThumbnail(@PathVariable Long pageId, @AuthenticationPrincipal User user){
//        try{
//            Page page = pageService.getById(pageId);
//            PageThumbnail pageThumbnail = pageThumbnailService.removeThumbnail(pageId);
//
//            return ResponseEntity.ok(pageThumbnail);
//        }
//        catch (PageNotFoundException e){
//            logger.error(HttpStatus.NOT_FOUND + ": page not found.");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
//        catch (NotFoundException e){
//            logger.error(HttpStatus.NO_CONTENT + ": Thumbnails is path is null.");
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
//        }
//        catch (Exception e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//        }
//    }
}
