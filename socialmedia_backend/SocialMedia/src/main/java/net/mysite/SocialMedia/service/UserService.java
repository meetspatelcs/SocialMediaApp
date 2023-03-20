package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.domain.Friend;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.dto.UserDto;
import net.mysite.SocialMedia.errors.NotFoundException;
import net.mysite.SocialMedia.errors.UserNotFoundException;
import net.mysite.SocialMedia.repository.FriendRepository;
import net.mysite.SocialMedia.repository.UserRepository;
import net.mysite.SocialMedia.util.CustomPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private CustomPasswordEncoder customPasswordEncoder;

    public Optional<User> findByUsername(User user){
        return userRepository.findByUsername(user.getUsername());
    }

    public Optional<User> findById(Long friendId){
        return userRepository.findById(friendId);
    }

    public Set<User> findAll(User user) {
        Set<User> allUsers = getAllUsers();
        Set<Friend> friendList = friendRepository.findAllFriends(user);
        Set<User> userSet = removeFriendsFromList(allUsers, friendList);
        userSet.remove(user);
        return userSet;
    }
    private Set<User> getAllUsers(){
        List<User> tempList = userRepository.findAll();
        Set<User> newUserList = new HashSet<>();
        newUserList.addAll(tempList);
        return newUserList;
    }
    private Set<User> removeFriendsFromList(Set<User> users, Set<Friend> friends){
        Set<User> newSetFriendsTOAdd = new HashSet<>();

        for(Friend myFriend : friends){

            if(users.contains(myFriend.getUser())){
                users.remove(myFriend.getUser());
            }
            if(users.contains(myFriend.getRequestedUser())){
                users.remove(myFriend.getRequestedUser());
            }

        }
        newSetFriendsTOAdd.addAll(users);

        return newSetFriendsTOAdd;
    }

    public User createUser(UserDto userDto) {
        User newUser = new User();
        newUser.setFirstname(userDto.getFirstname());
        newUser.setLastname(userDto.getLastname());

        newUser.setDob(convertDate(userDto.getDob()));
        newUser.setUsername(userDto.getUsername());
        String encodedPassword = customPasswordEncoder.getPasswordEncoder().encode(userDto.getPassword());
        newUser.setPassword(encodedPassword);

        newUser.setIdentification(userDto.getIdentification());
        newUser.setCreatedOn(LocalDate.now());
        newUser.setIsActive('0');

        userRepository.save(newUser);
        return newUser;
    }

    private Date convertDate(String date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<User> getNotFriendsWithUser(User user) {
        Set<User> userSet;
        userSet = userRepository.findUserByNonFriends(user);
        if(userSet.isEmpty()){ throw new NotFoundException("Users not found to add in friends list.");}
        return userSet;
    }

    public Set<User> getUserBySearchTerm(String searchTerm, User user) {
        Set<User> userSet;
        userSet = userRepository.findUserByNameOrEmail(searchTerm, user.getId());
        if(userSet.isEmpty()){ throw new UserNotFoundException("User was not found.");}
        return userSet;
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean isIdTaken(String Identification) {
        return userRepository.findByIdentification(Identification).isPresent();
    }
}
