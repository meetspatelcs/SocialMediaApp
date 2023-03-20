package net.mysite.SocialMedia.web;

import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.dto.UserDto;
import net.mysite.SocialMedia.errors.NotFoundException;
import net.mysite.SocialMedia.errors.UserNotFoundException;
import net.mysite.SocialMedia.service.InfoService;
import net.mysite.SocialMedia.service.PostService;
import net.mysite.SocialMedia.service.UserService;
import net.mysite.SocialMedia.service.UserThumbnailService;
import net.mysite.SocialMedia.util.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    @Autowired
    private UserService userService;
    @Autowired
    private InfoService infoService;
    @Autowired
    private UserThumbnailService userThumbnailService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTUtil jwtUtil;

    @GetMapping("myDetails")
    public ResponseEntity<?> getMyDetails(@AuthenticationPrincipal User user){
//        Optional<User> myDetails = userService.findByUsername(user);
//        return ResponseEntity.ok(myDetails);
        try{
            Optional<User> myDetails = userService.findByUsername(user);

            if(myDetails.isPresent()){
                return ResponseEntity.ok(myDetails.get());
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }
        catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (DataAccessException e){
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Failed to fetch user details. Please try again later.");
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @GetMapping("/notFriend")
    public ResponseEntity<?> getUsers(@AuthenticationPrincipal User user){
//        Set<User> allUsers = userService.findAll(user);
//        Set<User> allUsers = userService.getNotFriendsWithUser(user);
//        return ResponseEntity.ok(allUsers);

        try{
            Set<User> allUsers = userService.getNotFriendsWithUser(user);
            return ResponseEntity.ok(allUsers);
        }
        catch (NotFoundException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> getUserBySearchField(@RequestParam("friend") String searchTerm, @AuthenticationPrincipal User user){
//        Set<User> userSet = userService.getUserBySearchTerm(searchTerm, user);
//        return ResponseEntity.ok(userSet);
        try{
            Set<User> userSet = userService.getUserBySearchTerm(searchTerm, user);
            return ResponseEntity.ok(userSet);
        }
        catch (UserNotFoundException e){
            logger.error("User not found for search term: " + searchTerm);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            logger.error("An error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @PostMapping("register")
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto){

        // validate if there is no duplicate username and identification/id

//        User newUser = userService.createUser(userDto);
//        infoService.save(userDto, newUser);


//        System.out.println(userDto.getFirstname() + " : " + userDto.getIdentification());

//        try{
//            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));
//            User user = (User) authentication.getPrincipal();
//            user.setPassword(null);
//
//            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, jwtUtil.generateToken(user)).body(user);
//        }
//        catch (BadCredentialsException ex){
//            System.out.println(userDto.getFirstname() + " : " + userDto.getIdentification());
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }

        try{
            if(userService.isUsernameTaken(userDto.getUsername())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists!");
            }
            if(userService.isIdTaken(userDto.getIdentification())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID already exists!");
            }

            User newUser = userService.createUser(userDto);
            infoService.save(userDto, newUser);
            userThumbnailService.save(newUser);

            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully!");
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating user");
        }

    }

}
