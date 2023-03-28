package net.mysite.SocialMedia.web;

import net.mysite.SocialMedia.domain.Info;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.dto.UserEditDto;
import net.mysite.SocialMedia.err.UserNotFoundException;
import net.mysite.SocialMedia.service.InfoService;
import net.mysite.SocialMedia.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/UsersInfo")
public class InfoController {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    @Autowired
    private InfoService infoService;

    // fetch info of the logged user
    @GetMapping("/user")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal User user){
        try{
            Info myInfo = infoService.getUserInfoHandler(user);
            return ResponseEntity.ok(myInfo);
        }
        catch (NoSuchElementException e){
            logger.error(HttpStatus.NOT_FOUND + ": " + e.getMessage() + " Error occurred in info Entity.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    // fetch info of the visited user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getVisitUserInfo(@PathVariable Long userId){
        try{
            Info visitUserInfo = infoService.getUserInfo(userId);
            return ResponseEntity.ok(visitUserInfo);
        }
        catch (NoSuchElementException e){
            logger.error("USERID: " + userId);
            logger.error(HttpStatus.NOT_FOUND + ": " + e.getMessage() + " Error occurred in info Entity." );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @PutMapping("{personalInfoId}")
    public ResponseEntity<?> updateUserAndInfo(@RequestBody UserEditDto userEditDto,
                                               @PathVariable Long personalInfoId,
                                               @AuthenticationPrincipal User user){
        try{
            Info validateInfo = infoService.getInfoById(personalInfoId);
            Info updatedInfo = infoService.updateUserInfo(userEditDto, validateInfo, user);
            updatedInfo.setUser(null);
            return ResponseEntity.ok(updatedInfo);
        }
        catch (NoSuchElementException e){
            logger.error(HttpStatus.NOT_FOUND + "Failed to find data in database, entity Info. ID: " + personalInfoId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (UserNotFoundException e){
            logger.error(HttpStatus.CONFLICT + e.getMessage() + "The Entity UserInfo could not find any ref to User with ID: " + personalInfoId + "and other info.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        catch (Exception e){
            logger.error("Error updating user info.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating user info.");
        }
    }
}