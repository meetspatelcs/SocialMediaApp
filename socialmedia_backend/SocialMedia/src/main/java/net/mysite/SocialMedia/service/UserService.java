package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.dto.UserDto;
import net.mysite.SocialMedia.err.IdentificationException;
import net.mysite.SocialMedia.err.UserNotFoundException;
import net.mysite.SocialMedia.err.UsernameAlreadyTakenException;

import java.util.*;

public interface UserService {

     Optional<User> findByUsername(User user);

     Optional<User> findById(Long friendId) throws UserNotFoundException;

     User createUser(UserDto userDto) throws IllegalArgumentException, UsernameAlreadyTakenException, IdentificationException;

     Set<User> getNotFriendsWithUser(User user);

     Set<User> getUserBySearchTerm(String searchTerm, User user) throws IllegalArgumentException, UserNotFoundException;

     boolean isUsernameTaken(String username);

     boolean isIdTaken(String Identification);
}
