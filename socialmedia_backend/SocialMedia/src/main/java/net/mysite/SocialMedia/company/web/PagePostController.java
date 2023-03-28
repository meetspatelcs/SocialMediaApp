package net.mysite.SocialMedia.company.web;

import net.mysite.SocialMedia.company.domain.Page;
import net.mysite.SocialMedia.company.domain.PagePost;
import net.mysite.SocialMedia.company.domain.PagePostPhoto;
import net.mysite.SocialMedia.company.sevice.PagePostPhotoService;
import net.mysite.SocialMedia.company.sevice.PagePostService;
import net.mysite.SocialMedia.company.sevice.PageService;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.err.MissingFieldException;
import net.mysite.SocialMedia.err.PageNotFoundException;
import net.mysite.SocialMedia.err.PagePostMediaNotFoundException;
import net.mysite.SocialMedia.err.PagePostNotFoundException;
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
            PagePost newPagePost = pagePostService.save(pageId, description);
            PagePostPhoto newPPostPhoto = pagePostPhotoService.save(newPagePost, myFile, user);
            return ResponseEntity.ok(newPPostPhoto);
        }
        catch (MissingFieldException e){
            logger.error(HttpStatus.BAD_REQUEST + ": error in pagePost entity." + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (PageNotFoundException e){
            logger.error(HttpStatus.BAD_REQUEST + ": error in page entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (PagePostNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in pagePost entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (IOException e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("/page/{pageId}/allPosts")
    public ResponseEntity<?> getAllPagePosts(@PathVariable Long pageId){
        try{
            Page myPage = pageService.getById(pageId);
            Set<PagePost> allPagePosts = pagePostService.getAllByPage(myPage);
            return ResponseEntity.ok(allPagePosts);
        }
        catch (PageNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in page entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("/page/{pageId}/posts/{postId}")
    public ResponseEntity<?> getPagePostById(@PathVariable Long pageId, @PathVariable Long postId){
        try{
            PagePostPhoto myPost = pagePostPhotoService.getById(pageId,  postId);
            return ResponseEntity.ok(myPost);
        }
        catch (PagePostMediaNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in pagePostPhoto entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("/page/{pageId}/postImg/{postId}")
    public ResponseEntity<?> getImg(@PathVariable Long pageId, @PathVariable Long postId) throws IOException {
        try{
            byte[] imgBuff = pagePostPhotoService.getImgById(pageId, postId);
            return ResponseEntity.ok(imgBuff);
        }
        catch (PagePostMediaNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in pagePostPhoto entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (NullPointerException e){
            logger.error(HttpStatus.NO_CONTENT + ": error in pagePostPhoto entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        }
        catch (IOException e){
            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + ": error in pagePostPhoto entity. " +  "Something went wrong.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("/page/{pageId}/postVid/{pagePostId}")
    public Mono<ResponseEntity<?>> getVideo(@PathVariable Long pageId,
                                            @PathVariable Long pagePostId,
                                            @RequestHeader(value = "range", required  = false) String httpRangeList)  {
        try{
            return Mono.just(pagePostPhotoService.getVideoById(pageId, pagePostId, httpRangeList));
        }
        catch (PagePostMediaNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in pagePostPhoto entity. " + e.getMessage());
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()));
        }
        catch (NullPointerException e){
            logger.error(HttpStatus.NO_CONTENT + ": error in pagePostPhoto entity. " + e.getMessage());
            return Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage()));
        }
        catch (IOException e){ return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.")); }
    }

    @DeleteMapping("/page/{pageId}/posts/{postId}")
    public ResponseEntity<?> removePostOnPage(@PathVariable Long pageId, @PathVariable Long postId){
        try{
            pagePostService.removeById(pageId, postId);
            return ResponseEntity.ok("Post has been deleted!");
        }
        catch (PageNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in page entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
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
            if(myFile != null){ pagePostPhotoService.updateSave(pagePostPhoto, myFile, user);}
            return ResponseEntity.ok(pagePostPhoto);
        }
        catch (PagePostNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in pagePost entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (PagePostMediaNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in pagePostPhoto entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (IOException e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong."); }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("/page/{pageId}/allMediaInfo/{mediaType}")
    public ResponseEntity<?> generateMediaInfoList(@PathVariable Long pageId, @PathVariable String mediaType){
        try{
            Page myPage = pageService.getById(pageId);
            Set<PagePostPhoto> mediaInfoList = pagePostService.getMediaInfoList(myPage, mediaType);
            return ResponseEntity.ok(mediaInfoList);
        }
        catch (PageNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in page entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (PagePostNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in pagePost entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }
}