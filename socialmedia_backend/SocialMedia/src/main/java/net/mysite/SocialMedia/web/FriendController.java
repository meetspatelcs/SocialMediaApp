package net.mysite.SocialMedia.web;

import net.mysite.SocialMedia.domain.Friend;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.dto.FriendDto;
import net.mysite.SocialMedia.dto.FriendIdDto;
import net.mysite.SocialMedia.err.*;
import net.mysite.SocialMedia.err.UserNotFoundException;
import net.mysite.SocialMedia.service.FriendRecommendationService;
import net.mysite.SocialMedia.service.FriendService;
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
@RequestMapping("/api/friends")
public class FriendController {

    private static final Logger logger = LoggerFactory.getLogger(FriendController.class);
    @Autowired
    private FriendService friendService;
    @Autowired
    private FriendRecommendationService friendRecommendationService;

    @GetMapping("")
    public ResponseEntity<?> getUsersFriend(@AuthenticationPrincipal User user){
        try{
            Set<Friend> myFriends = friendService.findById(user);
            return ResponseEntity.ok(myFriends);
        }
        catch (EmptyFriendsException e){
            logger.error(HttpStatus.NO_CONTENT + ": error in friends entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("/user/{friendId}/validateFriend")
    public ResponseEntity<?> getRelationshipWithFriend(@PathVariable Long friendId, @AuthenticationPrincipal User user){
        try{
            Friend friendStat = friendService.getByUserAndFriend(friendId, user);
            return ResponseEntity.ok(friendStat);
        }
        catch (NotFriendException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in friends entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUsersFriendList(@PathVariable Long userId, @AuthenticationPrincipal User user){
        try{
            Set<User> friendList = friendService.getFriendList(userId);
            return ResponseEntity.ok(friendList);
        }
        catch (UserNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in friend entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (EmptyFriendsException e){
            logger.error(HttpStatus.NO_CONTENT + ": error in friends entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("/user/{userId}/all")
    public ResponseEntity<?> getUserFriendAll(@PathVariable Long userId, @AuthenticationPrincipal User user){
        try{
            Set<FriendIdDto> friendLis = friendService.getFriendListAll(userId);
            return ResponseEntity.ok(friendLis);
        }
        catch (UserNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in friend entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (EmptyFriendsException e){
            logger.error(HttpStatus.NO_CONTENT + ": error in friends entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    // TODO: Check the mapping in frontend
    @GetMapping("{friendId}")
    public ResponseEntity<?> getFriendElement(@PathVariable Long friendId){
        try{
            Optional<Friend> friendOpt = friendService.findById(friendId);
            return ResponseEntity.ok(new FriendDto(friendOpt.orElse(new Friend())));
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @PostMapping("/add/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable Long friendId, @AuthenticationPrincipal User user){
        try{
            Friend newFriend = friendService.save(friendId, user);
            return ResponseEntity.ok(newFriend);
        }
        catch (UserNotFoundException e){
            logger.error(HttpStatus.NOT_FOUND + ": error in friend entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (InvalidFriendRequestException e){
            logger.error(HttpStatus.BAD_REQUEST + ": error in friend entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (FriendRequestAlreadyExistsException e){
            logger.error(HttpStatus.CONFLICT + ": error occurred in friends entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @PutMapping("{friendId}")
    public ResponseEntity<?> updateFriend(@PathVariable Long friendId,
                                          @RequestBody Friend friend,
                                          @AuthenticationPrincipal User user){
        try{
            Friend approveFriend = friendService.save(friend);
            return ResponseEntity.ok(approveFriend);
        }
        catch (MissingFieldException e){
            logger.error(HttpStatus.BAD_REQUEST + ": error occurred in friend entity. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @DeleteMapping("{friendId}")
    public ResponseEntity<?> deleteFriend(@PathVariable Long friendId){
        try{
            friendService.deleteById(friendId);
            return ResponseEntity.ok("Friend has been removed.");
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @DeleteMapping("{friendId}/unfriend")
    public ResponseEntity<?> findAndDeleteFriend(@PathVariable Long friendId, @AuthenticationPrincipal User user){
        try{
            friendService.deleteFriendByUserAndFriend(friendId, user);
            return ResponseEntity.ok("Friend has been removed. 1:26:28");
        }
        catch (NotFriendException e){
            logger.error(HttpStatus.NOT_FOUND + ": " + e.getMessage() + " Cannot delete a friend who are not friends.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred."); }
    }

    @GetMapping("/user/{friendId}/friendStatus")
    public ResponseEntity<?> getRelationShipWithUserid(@PathVariable Long friendId, @AuthenticationPrincipal User user){
        try{
            FriendIdDto friendStat = friendService.getStatByIds(friendId, user.getId());
            return ResponseEntity.ok(friendStat);
        }
        catch (Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); }
    }

    @GetMapping("/recommendation")
    public ResponseEntity<?> getRecommendedUsers(@AuthenticationPrincipal User user){
        Set<User> userSet = friendRecommendationService.generateNewFriendRecommendations(user);
        return ResponseEntity.ok(userSet);
    }
}