package net.mysite.SocialMedia.company.web;

import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.company.domain.PagePost;
import net.mysite.SocialMedia.company.domain.PagePostPhoto;
import net.mysite.SocialMedia.company.sevice.PagePostPhotoService;
import net.mysite.SocialMedia.company.sevice.PagePostService;
import net.mysite.SocialMedia.company.sevice.PageService;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.errors.ImageNotFoundException;
import net.mysite.SocialMedia.errors.InvalidDescriptionException;
import net.mysite.SocialMedia.errors.PageNotFoundException;
import net.mysite.SocialMedia.errors.PostNotFoundException;
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
@RequestMapping("/api/pagePosts")
public class PagePostController {

    @Autowired
    private PagePostService pagePostService;
    @Autowired
    private PagePostPhotoService pagePostPhotoService;
    @Autowired
    private PageService pageService;
    private final Logger logger = LoggerFactory.getLogger(PagePostPhotoService.class);
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";

    @PostMapping(value = "/page/{pageId}/createPagePost", consumes = {MULTIPART_FORM_DATA})
    public ResponseEntity<?> createNewPostOnPage(@PathVariable Long pageId,
                                                 @RequestParam(value = "myFile", required = false) MultipartFile myFile,
                                                 @RequestParam(value = "description", required = true) String description,
                                                 @AuthenticationPrincipal User user) throws IOException {
        try{
            if(pageId == null || description.trim().isEmpty()){
                throw new InvalidDescriptionException("Description cannot be null or empty.");
            }

            PagePost newPagePost = pagePostService.save(pageId, description);
            PagePostPhoto newPPostPhoto = pagePostPhotoService.save(newPagePost, myFile, user);

            return ResponseEntity.ok(newPPostPhoto);
        }
        catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        catch (InvalidDescriptionException e){
            logger.error(HttpStatus.BAD_REQUEST + ": description can not be empty for page post.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (DataAccessException e){
            logger.error(HttpStatus.BAD_REQUEST + ": failed to create post for page: " + pageId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e){
            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + ": an error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @GetMapping("/page/{pageId}/allPosts")
    public ResponseEntity<?> getAllPagePosts(@PathVariable Long pageId){
        try{
            Page myPage = pageService.getById(pageId);
            Set<PagePost> allPagePosts = pagePostService.getAllByPage(myPage);
            return ResponseEntity.ok(allPagePosts);
        }
        catch (PageNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ":" + "Page with ID " + pageId + " not found in Entity Page");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (NullPointerException e){
            logger.error(HttpStatus.NOT_FOUND + ": No posts are found in entity page With ID " + pageId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + "An error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @GetMapping("/page/{pageId}/posts/{postId}")
    public ResponseEntity<?> getPagePostById(@PathVariable Long pageId, @PathVariable Long postId){
        try{
            PagePostPhoto myPost = pagePostPhotoService.getById(pageId,  postId);
            return ResponseEntity.ok(myPost);
        }
        catch (PostNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @GetMapping("/page/{pageId}/postImg/{postId}")
    public ResponseEntity<?> getImg(@PathVariable Long pageId, @PathVariable Long postId) throws IOException {
        try{
            byte[] imgBuff = pagePostPhotoService.getImgById(pageId, postId);
            return ResponseEntity.ok(imgBuff);
        }
        catch (NullPointerException e){
            logger.error(HttpStatus.BAD_REQUEST + "img was not for post with ID " + postId + " in PagePost entity.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (ImageNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": post was not found in entity pagePost.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + ": an error occurred in pagePost entity.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @GetMapping("/page/{pageId}/postVid/{pagePostId}")
    public Mono<ResponseEntity<?>> getVideo(@PathVariable Long pageId,
                                            @PathVariable Long pagePostId,
                                            @RequestHeader(value = "range", required  = false) String httpRangeList)  {
        try{
            return Mono.just(pagePostPhotoService.getVideoById(pageId, pagePostId, httpRangeList));
        }
        catch (IOException e){
            logger.error(HttpStatus.BAD_REQUEST + "Something went wrong while fetching video for page.");
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

    //    TODO: try catch below, not done all catches
    @DeleteMapping("/page/{pageId}/posts/{postId}")
    public ResponseEntity<?> removePostOnPage(@PathVariable Long pageId, @PathVariable Long postId){
        try{
            pagePostService.removeById(pageId, postId);
            return ResponseEntity.ok("Post has been deleted!");
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(value = "/page/{pageId}/posts/{postId}", consumes = {MULTIPART_FORM_DATA})
    public ResponseEntity<?> updatePagePostById(@PathVariable Long pageId,
                                                @PathVariable Long postId,
                                                @RequestParam(value = "myFile", required = false) MultipartFile myFile,
                                                @RequestParam(value = "description", required = true) String description,
                                                @AuthenticationPrincipal User user) throws IOException {
        try{
            PagePost updatePost = pagePostService.UpdatePagePostById(postId, description);
            PagePostPhoto pagePostPhoto = pagePostPhotoService.getById(pageId, postId);

            if(pagePostPhoto != null && myFile != null){ pagePostPhotoService.updateSave(pagePostPhoto, myFile, user);}

            return ResponseEntity.ok(pagePostPhoto);
        }
        catch (IOException e){
            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + ": an error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
        catch (PostNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": post was not found on page: " + pageId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + ": an error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @GetMapping("/page/{pageId}/allMediaInfo/{mediaType}")
    public ResponseEntity<?> generateMediaInfoList(@PathVariable Long pageId, @PathVariable String mediaType){
        try{
            Page myPage = pageService.getById(pageId);
            Set<PagePostPhoto> mediaInfoList = pagePostService.getMediaInfoList(myPage, mediaType);
            return ResponseEntity.ok(mediaInfoList);
        }
        catch (PageNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": Page not found in entity Page, ID " + pageId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (NullPointerException e){
            logger.error(HttpStatus.NOT_FOUND + ": No Page Post Photo content.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        }
        catch (Exception e){
            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + ": An error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }
}