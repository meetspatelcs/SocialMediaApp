package net.mysite.SocialMedia.web;

import net.mysite.SocialMedia.company.sevice.PagePostPhotoService;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.domain.UserThumbnail;
import net.mysite.SocialMedia.err.ThumbnailNotFoundException;
import net.mysite.SocialMedia.service.UserThumbnailService;
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
@RequestMapping("/api/userThumbnails")
public class UserThumbController {

    private final Logger logger = LoggerFactory.getLogger(PagePostPhotoService.class);
    @Autowired
    private UserThumbnailService userThumbnailService;
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";

    @GetMapping("")
    public ResponseEntity<?> getThumbnail(@AuthenticationPrincipal User user){
        try{
            byte[] imgByte = userThumbnailService.getImgById(user);
            return ResponseEntity.ok(imgByte);
        }
        catch (ThumbnailNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in userThumbnail entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (NullPointerException e){
            logger.error(HttpStatus.NO_CONTENT + ": error in userThumbnail entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        }
        catch (IOException e){
            logger.error(HttpStatus.NOT_FOUND + ": failed to find thumbnail. Error in UserThumbnail entity.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @PutMapping(value = "", consumes = {MULTIPART_FORM_DATA})
    public ResponseEntity<?> updateThumbnail( @RequestParam(value = "myFile", required = false) MultipartFile myFile,
                                              @AuthenticationPrincipal User user) throws IOException {
        try{
            UserThumbnail updatedThumbnail = userThumbnailService.updateThumbnail(user, myFile);
            return ResponseEntity.ok(updatedThumbnail);
        }
        catch (ThumbnailNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": Failed to find a thumbnail for user: " + user.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (IOException e){
            logger.error(HttpStatus.BAD_REQUEST + ": Failed to update thumbnail for user: " + user.getId() + " in userThumbnail Entity.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (NullPointerException e){
            logger.error(HttpStatus.UNPROCESSABLE_ENTITY + ": Failed to update the thumbnail, in userThumbnail Entity." + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); }
    }

    @PutMapping("/remove")
    public ResponseEntity<?> removeThumbnail(@AuthenticationPrincipal User user){
        try{
            UserThumbnail removeThumbnail = userThumbnailService.removeThumbnail(user);
            return ResponseEntity.ok(removeThumbnail);
        }
        catch (ThumbnailNotFoundException e){
            logger.error(HttpStatus.NO_CONTENT + ": Failed to find UserThumbnail in entity.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }
}
