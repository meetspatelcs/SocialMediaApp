package net.mysite.SocialMedia.web;

import net.mysite.SocialMedia.domain.Friend;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.dto.FriendDto;
import net.mysite.SocialMedia.dto.FriendIdDto;
import net.mysite.SocialMedia.errors.*;
import net.mysite.SocialMedia.service.FriendService;
import net.mysite.SocialMedia.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.Set;


@RestController
@RequestMapping("/api/friends")
public class FriendController {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    @Autowired
    private FriendService friendService;

    @GetMapping("")
    public ResponseEntity<?> getUsersFriend(@AuthenticationPrincipal User user){
        try{
            Set<Friend> myFriends = friendService.findById(user);
            return ResponseEntity.ok(myFriends);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get user's friends.");
        }
    }

    @GetMapping("/user/{friendId}/validateFriend")
    public ResponseEntity<?> getRelationshipWithFriend(@PathVariable Long friendId, @AuthenticationPrincipal User user){
        try{
            Friend friendStat = friendService.getByUserAndFriend(friendId, user);
            return ResponseEntity.ok(friendStat);
        } catch (FriendNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while getting friend status.");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUsersFriendList(@PathVariable Long userId, @AuthenticationPrincipal User user){
        try{
            Set<User> friendList = friendService.getFriendList(userId);
            return ResponseEntity.ok(friendList);
        }
        catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}/all")
    public ResponseEntity<?> getUserFriendAll(@PathVariable Long userId, @AuthenticationPrincipal User user){
        try{
            Set<FriendIdDto> friendLis = friendService.getFriendListAll(userId);
            return ResponseEntity.ok(friendLis);
        }
        catch (FriendListNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("{friendId}")
    public ResponseEntity<?> getFriendElement(@PathVariable  Long friendId){
        try{
            Optional<Friend> friendOpt = friendService.findById(friendId);
            return ResponseEntity.ok(new FriendDto(friendOpt.orElse(new Friend())));
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/add/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable Long friendId, @AuthenticationPrincipal User user){
        try{
            Friend newFriend = friendService.save(friendId, user);
            return ResponseEntity.ok(newFriend);
        }
        catch (FriendRequestException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (DataAccessException e){
            logger.error("Error adding friend: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding friend: " + e.getMessage());
        }
        catch (Exception e){
            logger.error("Unexpected error adding friend: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error adding friend: " + e.getMessage());
        }
    }

    @PutMapping("{friendId}")
    public ResponseEntity<?> updateFriend(@PathVariable Long friendId, @RequestBody Friend friend, @AuthenticationPrincipal User user){
        try{
            Friend approveFriend = friendService.save(friend);
            return ResponseEntity.ok(approveFriend);
        }
        catch (IllegalArgumentException | NullPointerException | DataAccessException e){
            logger.error("An error occurred while saving the friend object", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("An error occurred while saving the friend object: " + e.getMessage());
        }
    }

    @DeleteMapping("{friendId}")
    public ResponseEntity<?> deleteFriend(@PathVariable Long friendId){
        try{
            friendService.deleteById(friendId);
            return ResponseEntity.ok("Friend has been removed");
        }
        catch (FriendNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + "Friend was not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friend with ID " + friendId + " does not exist");
        }
        catch (Exception e){
            logger.error(HttpStatus.INTERNAL_SERVER_ERROR + ": an error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the friend: " + e.getMessage());
        }
    }

    @DeleteMapping("{friendId}/unfriend")
    public ResponseEntity<?> findAndDeleteFriend(@PathVariable Long friendId, @AuthenticationPrincipal User user){
        try{
            friendService.deleteFriendByUserAndFriend(friendId, user);
            return ResponseEntity.ok("Friend has been removed. 1:26:28");
        }
        catch (DataAccessException e){
            logger.error(HttpStatus.BAD_REQUEST + ": Failed to remove friend.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (NotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": friend was not found with friendId: " + friendId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{friendId}/friendStatus")
    public ResponseEntity<?> getRelationShipWithUserid(@PathVariable Long friendId, @AuthenticationPrincipal User user){
        try{
            FriendIdDto friendStat = friendService.getStatByIds(friendId, user.getId());
            return ResponseEntity.ok(friendStat);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
