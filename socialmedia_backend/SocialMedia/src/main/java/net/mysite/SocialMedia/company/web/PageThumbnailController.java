package net.mysite.SocialMedia.company.web;

import net.mysite.SocialMedia.company.domain.PageThumbnail;
import net.mysite.SocialMedia.company.sevice.PagePostPhotoService;
import net.mysite.SocialMedia.company.sevice.PageThumbnailService;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.err.PageNotFoundException;
import net.mysite.SocialMedia.err.PageThumbnailNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/pageThumbnails")
public class PageThumbnailController {

    @Autowired
    private PageThumbnailService pageThumbnailService;
    private final Logger logger = LoggerFactory.getLogger(PagePostPhotoService.class);
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";

    @GetMapping("/page/{pageId}/thumbnails")
    public ResponseEntity<?> getPageThumbnail(@AuthenticationPrincipal User user, @PathVariable Long pageId){
        try{
            byte[] thumbnail = pageThumbnailService.getThumbnail(pageId);
            return ResponseEntity.ok(thumbnail);
        }
        catch (PageThumbnailNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in page thumbnail entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (NullPointerException e){
            logger.error(HttpStatus.NO_CONTENT + ": error in page thumbnail entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @PutMapping(value = "/page/{pageId}/thumbnails", consumes = {MULTIPART_FORM_DATA})
    public ResponseEntity<?> updatePageThumbnail(@PathVariable Long pageId,
                                           @RequestParam(value = "myFile", required = false) MultipartFile myFile,
                                           @AuthenticationPrincipal User user) throws IOException {
        try{
            PageThumbnail updatePageThumbnail = pageThumbnailService.save(pageId, myFile, user);
            return ResponseEntity.ok(updatePageThumbnail);
        }
        catch (PageThumbnailNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in page thumbnail entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (IOException e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    // this removes the ref to thumbnail path, so it becomes null
    @PutMapping("/page/{pageId}/thumbnails/remove")
    public ResponseEntity<?> removePageThumbnail(@PathVariable Long pageId, @AuthenticationPrincipal User user){
        try{
            PageThumbnail pageThumbnail = pageThumbnailService.removeThumbnail(pageId);
            return ResponseEntity.ok(pageThumbnail);
        }
        catch (PageNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": page not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (PageThumbnailNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in page thumbnail entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }
}
