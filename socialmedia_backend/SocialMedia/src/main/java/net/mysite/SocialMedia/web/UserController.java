package net.mysite.SocialMedia.web;

import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.dto.UserDto;
import net.mysite.SocialMedia.err.IdentificationException;
import net.mysite.SocialMedia.err.UsernameAlreadyTakenException;
import net.mysite.SocialMedia.err.UserNotFoundException;
import net.mysite.SocialMedia.service.InfoService;
import net.mysite.SocialMedia.service.UserService;
import net.mysite.SocialMedia.service.UserThumbnailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private InfoService infoService;
    @Autowired
    private UserThumbnailService userThumbnailService;

    @GetMapping("myDetails")
    public ResponseEntity<?> getMyDetails(@AuthenticationPrincipal User user){
        try{
            Optional<User> myDetails = userService.findByUsername(user);
            return ResponseEntity.ok(myDetails.get());
        }
        catch (UserNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in user entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("/notFriend")
    public ResponseEntity<?> getUsers(@AuthenticationPrincipal User user){
        try{
            Set<User> allUsers = userService.getNotFriendsWithUser(user);
            return ResponseEntity.ok(allUsers);
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred"); }
    }

    @GetMapping("/search")
    public ResponseEntity<?> getUserBySearchField(@RequestParam("friend") String searchTerm, @AuthenticationPrincipal User user){
        try{
            Set<User> userSet = userService.getUserBySearchTerm(searchTerm, user);
            return ResponseEntity.ok(userSet);
        }
        catch (IllegalArgumentException e){
            logger.error(HttpStatus.BAD_REQUEST + ": Failed to find user based on search term. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (UserNotFoundException e){
            logger.error("User not found for search term: " + searchTerm);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @PostMapping("register")
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto){
        try{
            User newUser = userService.createUser(userDto);
            infoService.save(userDto, newUser);
            userThumbnailService.save(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully!");
        }
        catch (IllegalArgumentException e){
            logger.error(HttpStatus.BAD_REQUEST + "Error occurred while creating a user in User Entity.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (UsernameAlreadyTakenException e){
            logger.error(HttpStatus.BAD_REQUEST + "Error occurred while creating a user in User Entity." + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (IdentificationException e){
            logger.error(HttpStatus.BAD_REQUEST + ": error in user entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }
}
