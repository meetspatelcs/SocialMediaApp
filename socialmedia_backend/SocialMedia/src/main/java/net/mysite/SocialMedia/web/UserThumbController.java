package net.mysite.SocialMedia.web;

import net.mysite.SocialMedia.company.sevice.PagePostPhotoService;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.domain.UserThumbnail;
import net.mysite.SocialMedia.errors.NotFoundException;
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
        catch (NullPointerException e){
            logger.error(HttpStatus.NO_CONTENT + ": no thumbnail byte found of user.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        }
        catch (NotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": not found in thumbnail entity.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + ": an error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(value = "", consumes = {MULTIPART_FORM_DATA})
    public ResponseEntity<?> updateThumbnail( @RequestParam(value = "myFile", required = false) MultipartFile myFile,
                                              @AuthenticationPrincipal User user) throws IOException {

        try{
            UserThumbnail updatedThumbnail = userThumbnailService.updateThumbnail(user, myFile);
            return ResponseEntity.ok(updatedThumbnail);
        }
        catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/remove")
    public ResponseEntity<?> removeThumbnail(@AuthenticationPrincipal User user){
        try{
            UserThumbnail removeThumbnail = userThumbnailService.removeThumbnail(user);
            return ResponseEntity.ok(removeThumbnail);
        }
        catch (NotFoundException e){
            logger.error(HttpStatus.NO_CONTENT + ": no thumbnail byte for user.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
