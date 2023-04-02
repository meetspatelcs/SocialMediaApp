package net.mysite.SocialMedia.service;

import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.dto.UserDto;
import net.mysite.SocialMedia.err.IdentificationException;
import net.mysite.SocialMedia.err.UserNotFoundException;
import net.mysite.SocialMedia.err.UsernameAlreadyTakenException;
import net.mysite.SocialMedia.repository.UserRepository;
import net.mysite.SocialMedia.util.CustomPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomPasswordEncoder customPasswordEncoder;

    @Override
    public Optional<User> findByUsername(User user){
        return userRepository.findByUsername(user.getUsername());
    }

    @Override
    public Optional<User> findById(Long friendId) throws UserNotFoundException{
        Optional<User> user = userRepository.findById(friendId);
        if(user.isEmpty()){ throw new UserNotFoundException("User does not exists."); }
        return user;
    }

    @Override
    public User createUser(UserDto userDto) throws IllegalArgumentException, UsernameAlreadyTakenException, IdentificationException {

        String first = userDto.getFirstname();
        String last = userDto.getLastname();
        String username = userDto.getUsername();
        String idNum = userDto.getIdentification();
        String date = userDto.getDob();
        String pass = userDto.getPassword();

        if(username.isEmpty()){ throw new IllegalArgumentException("Username cannot be null or empty."); }
        if(idNum.isEmpty()){ throw new IllegalArgumentException("Identification number cannot be null or empty."); }

        if(isUsernameTaken(userDto.getUsername())){ throw new UsernameAlreadyTakenException("This email address is already exists."); }
        if(isIdTaken(userDto.getIdentification())){ throw new IdentificationException("This ID already exists in database."); }

        if(first.isEmpty()){ throw new IllegalArgumentException("First name cannot be null or empty."); }
        if(last.isEmpty()){ throw new IllegalArgumentException("Last name cannot be null or empty."); }
        if(date.isEmpty()){ throw new IllegalArgumentException("Date of Birth name cannot be null or empty."); }
        if(pass.isEmpty()){ throw new IllegalArgumentException("Password cannot be null or empty."); }

        User newUser = new User();
        newUser.setFirstname(first);
        newUser.setLastname(last);

        newUser.setDob(convertDate(date));
        newUser.setUsername(username);
        String encodedPassword = customPasswordEncoder.getPasswordEncoder().encode(pass);
        newUser.setPassword(encodedPassword);

        newUser.setIdentification(idNum);
        newUser.setCreatedOn(LocalDate.now());
        newUser.setIsActive('0');

        userRepository.save(newUser);
        return newUser;
    }

    @Override
    public Set<User> getNotFriendsWithUser(User user) {
        Set<User> userSet = userRepository.findUserByNonFriends(user);
        if(userSet.isEmpty()){ return Collections.emptySet();}
        return userSet;
    }

    @Override
    public Set<User> getUserBySearchTerm(String searchTerm, User user) throws IllegalArgumentException, UserNotFoundException{
        if(searchTerm.isEmpty()){ throw new IllegalArgumentException("Search term cannot be empty."); }

        Set<User> userSet = userRepository.findUserByNameOrEmail(searchTerm, user.getId());
        if(userSet.isEmpty()){ throw new UserNotFoundException("User was not found.");}
        return userSet;
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public boolean isIdTaken(String Identification) { return userRepository.findByIdentification(Identification).isPresent(); }

    private Date convertDate(String date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return formatter.parse(date);
        }
        catch (ParseException e) { throw new RuntimeException(e); }
    }
}
